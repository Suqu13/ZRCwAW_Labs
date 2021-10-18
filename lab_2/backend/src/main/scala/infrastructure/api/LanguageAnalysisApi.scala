package infrastructure.api

import cats.Monad
import cats.effect.Async
import domain.spi.LanguageAnalysisService
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl.Http4sDsl

case class TextRequest(text: String)

class LanguageAnalysisApi[F[_] : Monad : Async](languageAnalysisService: LanguageAnalysisService[F]) extends HttpApi[F] {
  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case req@POST -> Root / "comprehend" / "language" =>
        req.decode[TextRequest] { r =>
          if (r.text.trim.isEmpty) BadRequest("Provide text to analysis")
          else Ok(languageAnalysisService.languageAnalysis(r.text))
        }
      case req@POST -> Root / "comprehend" / "sentiment" =>
        req.decode[TextRequest] { r =>
          if (r.text.trim.isEmpty) BadRequest("Provide text to analysis")
          else languageAnalysisService.sentimentAnalysis(r.text).foldF(
            e => NotFound(e.msg),
            Ok(_)
          )
        }

    }
  }
}
