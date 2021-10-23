package infrastructure.api

import cats.effect.Async
import cats.syntax.all._
import domain.model.ReadText
import domain.spi.ReadService
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl._
import org.http4s.headers.`Content-Type`
import org.http4s.{HttpRoutes, MediaType}

class ReadApi[F[_] : Async](readService: ReadService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case req@POST -> Root / "read" =>
        req.decode[ReadText.Input] { input =>
          for {
            x <- readService.read(input)
            z <- x.fold(
              e => NotFound(e.getMessage),
              output => Ok(
                output.voice,
                `Content-Type`(MediaType.audio.mp3)
              )
            )
          } yield z
        }
    }
  }
}
