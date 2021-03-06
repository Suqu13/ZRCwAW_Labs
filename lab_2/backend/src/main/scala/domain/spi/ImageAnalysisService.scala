package domain.spi

import domain.model.{ImageLabel, ImageText}

trait ImageAnalysisService[F[_]] {
  def getLabels(bucketName: String, imageName: String): F[Vector[ImageLabel]]
  def getTexts(bucketName: String, imageName: String): F[Vector[ImageText]]
}
