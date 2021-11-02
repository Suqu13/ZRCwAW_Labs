package infrastructure.api

import cats.Monad
import cats.effect.Async
import domain.model.User
import domain.spi.LanguageAnalysisService
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl

case class TextRequest(text: String)

class LanguageAnalysisApi[F[_] : Monad : Async](languageAnalysisService: LanguageAnalysisService[F]) extends AuthedHttpApi[F, User] {
  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of[User, F] {
      case authReq@POST -> Root / "comprehend" / "language" as _ =>
        authReq.req.decode[TextRequest] { r =>
          if (r.text.trim.isEmpty) BadRequest("Provide text to analysis")
          else Ok(languageAnalysisService.languageAnalysis(r.text))
        }
      case authReq@POST -> Root / "comprehend" / "sentiment" as _ =>
        authReq.req.decode[TextRequest] { r =>
          if (r.text.trim.isEmpty) BadRequest("Provide text to analysis")
          else languageAnalysisService.sentimentAnalysis(r.text).foldF(
            e => NotFound(e.msg),
            Ok(_)
          )
        }

    }
  }
}
