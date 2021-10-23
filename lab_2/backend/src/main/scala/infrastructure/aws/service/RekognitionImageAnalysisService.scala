package infrastructure.aws.service

import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.syntax.all._
import domain.model.{ImageLabel, ImageText}
import domain.spi.ImageAnalysisService
import software.amazon.awssdk.services.rekognition.RekognitionClient
import software.amazon.awssdk.services.rekognition.model.{DetectLabelsRequest, DetectTextRequest, Image, S3Object}

import scala.jdk.CollectionConverters.ListHasAsScala

class RekognitionImageAnalysisService[F[_] : Sync : Console](rekognitionClient: RekognitionClient) extends ImageAnalysisService[F] {
  override def getLabels(bucketName: String, imageName: String): F[Vector[ImageLabel]] =
    for {
      _ <- Sync[F].unit
      detectLabelsRequest = DetectLabelsRequest.builder()
        .image(prepareImage(bucketName, imageName))
        .maxLabels(10)
        .build()
      detectLabelsResponse <- Sync[F].blocking(rekognitionClient.detectLabels(detectLabelsRequest))
    } yield detectLabelsResponse.labels().asScala
      .map(label => ImageLabel(label.name(), label.confidence())).distinctBy(_.name)
      .toVector

  override def getTexts(bucketName: String, imageName: String): F[Vector[ImageText]] =
    for {
      _ <- Sync[F].unit
      detectTextRequest = DetectTextRequest.builder()
        .image(prepareImage(bucketName, imageName))
        .build()
      detectTextResponse <- Sync[F].blocking(rekognitionClient.detectText(detectTextRequest))
    } yield detectTextResponse.textDetections().asScala
      .map(detection => ImageText(detection.detectedText(), detection.confidence())).distinctBy(_.content)
      .toVector

  def prepareImage(bucketName: String, imageName: String): Image = {
    val s3Object = S3Object.builder()
      .bucket(bucketName)
      .name(imageName)
      .build()
    Image.builder()
      .s3Object(s3Object)
      .build()
  }
}
