package infrastructure.aws.service

import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.{Applicative, Functor}
import domain.model.ImageLabel
import domain.spi.ImageAnalysisService
import software.amazon.awssdk.services.rekognition.RekognitionClient
import software.amazon.awssdk.services.rekognition.model.{DetectLabelsRequest, Image, S3Object}

import scala.jdk.CollectionConverters.ListHasAsScala

class RekognitionImageAnalysisService[F[_] : Functor : Async : Console : Applicative](rekognitionClient: RekognitionClient) extends ImageAnalysisService[F] {
  override def getLabels(bucketName: String, imageName: String): F[Vector[ImageLabel]] = {
    val s3Object = S3Object.builder()
      .bucket(bucketName)
      .name(imageName)
      .build()
    val myImage = Image.builder()
      .s3Object(s3Object)
      .build()
    val detectLabelsRequest = DetectLabelsRequest.builder()
      .image(myImage)
      .maxLabels(10)
      .build()

    val response = rekognitionClient.detectLabels(detectLabelsRequest)
    val labels = response.labels().asScala
      .map(label => ImageLabel(label.name(), label.confidence()))
      .toVector

    println(labels)

    Sync[F].blocking(labels)
  }
}