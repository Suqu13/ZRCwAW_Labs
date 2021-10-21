package infrastructure.api

import cats.Monad
import cats.effect.Async
import cats.implicits._
import domain.spi.ImageAnalysisService
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._

class ImageAnalysisApi[F[_] : Monad : Async](imageAnalysisService: ImageAnalysisService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "rekognition" / bucketName / imageName =>
        imageAnalysisService.getLabels(bucketName, imageName).flatMap(Ok(_))
    }
  }
}
