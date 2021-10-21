package infrastructure.aws.service

import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.{Applicative, Functor}
import domain.model.{ImageLabel, ImageText}
import domain.spi.ImageAnalysisService
import software.amazon.awssdk.services.rekognition.RekognitionClient
import software.amazon.awssdk.services.rekognition.model.{DetectLabelsRequest, DetectTextRequest, Image, S3Object}

import scala.jdk.CollectionConverters.ListHasAsScala

class RekognitionImageAnalysisService[F[_] : Functor : Async : Console : Applicative](rekognitionClient: RekognitionClient) extends ImageAnalysisService[F] {
  override def getLabels(bucketName: String, imageName: String): F[Vector[ImageLabel]] = {
    val myImage = getImage(bucketName, imageName)

    val detectLabelsRequest = DetectLabelsRequest.builder()
      .image(myImage)
      .maxLabels(10)
      .build()

    val response = rekognitionClient.detectLabels(detectLabelsRequest)
    val labels = response.labels().asScala
      .map(label => ImageLabel(label.name(), label.confidence()))
      .toVector

    Sync[F].blocking(labels)
  }

  override def getText(bucketName: String, imageName: String): F[Vector[ImageText]] = {
    val myImage = getImage(bucketName, imageName)

    val detectLabelsRequest = DetectTextRequest.builder()
      .image(myImage)
      .build()

    val response = rekognitionClient.detectText(detectLabelsRequest)

    val detections = response.textDetections().asScala
      .map(detection => ImageText(detection.detectedText(), detection.confidence()))
      .toVector

    Sync[F].blocking(detections)
  }

  def getImage(bucketName: String, imageName: String): Image = {
    val s3Object = S3Object.builder()
      .bucket(bucketName)
      .name(imageName)
      .build()
    Image.builder()
      .s3Object(s3Object)
      .build()
  }
}