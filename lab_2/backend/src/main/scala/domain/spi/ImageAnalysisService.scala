package domain.spi

import domain.model.ImageLabel

trait ImageAnalysisService[F[_]] {
  def getLabels(bucketName: String, imageName: String): F[Vector[ImageLabel]]
}
