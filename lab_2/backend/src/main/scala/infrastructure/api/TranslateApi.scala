package infrastructure.api

import cats.effect.Async
import cats.syntax.all._
import domain.model.{TranslateFile, TranslateText}
import domain.spi._
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._
import org.http4s.headers.{`Content-Disposition`, `Content-Type`}
import org.http4s.multipart.Multipart
import org.http4s.{HttpRoutes, MediaType}
import org.typelevel.ci.CIString

class TranslateApi[F[_] : Async](translateService: TranslateService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      case req@POST -> Root / "translate" =>
        req.decode[TranslateText.Input] { input =>
          for {
            x <- translateService.translate(input)
            z <- x.fold(
              (e) => NotFound(e.getMessage),
              output => Ok(output)
            )
          } yield z
        }
      case req@POST -> Root / "translate" / "sourceLanguage" / sourceLanguage / "targetLanguage" / targetLanguage =>
        req.decode[Multipart[F]] { m =>
          val fileContent = m.parts.head.body
          val fileName = m.parts.head.filename.get
          for {
            x <- translateService.translateFile(TranslateFile.Input(fileContent, sourceLanguage, targetLanguage))
            z <- x.fold(
              e => NotFound(e.getMessage),
              output => Ok(
                output.fileContent,
                `Content-Type`(MediaType.application.`octet-stream`),
                `Content-Disposition`("attachment", Map(CIString("filename") -> fileName))
              )
            )
          } yield z
        }
    }
  }
}
