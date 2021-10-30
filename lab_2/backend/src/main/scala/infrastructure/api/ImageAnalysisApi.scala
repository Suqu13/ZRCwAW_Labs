package infrastructure.api

import cats.Monad
import cats.effect.Async
import cats.implicits._
import domain.model.User
import domain.spi.ImageAnalysisService
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._

class ImageAnalysisApi[F[_] : Monad : Async](imageAnalysisService: ImageAnalysisService[F]) extends AuthedHttpApi[F, User] {

  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of[User,F] {
      case GET -> Root / "rekognition" / bucketName / imageName / "labels" as _ =>
        imageAnalysisService.getLabels(bucketName, imageName).flatMap(Ok(_))
      case GET -> Root / "rekognition" / bucketName / imageName / "texts" as _ =>
        imageAnalysisService.getTexts(bucketName, imageName).flatMap(Ok(_))
    }
  }
}
