package infrastructure.api

import cats.Applicative
import cats.data.EitherT
import cats.effect.Async
import cats.implicits.toFunctorOps
import domain.model.Credentials
import domain.spi.UserService
import infrastructure.http.HttpApi
import infrastructure.security.Encryptor
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl._
import org.http4s.{HttpDate, HttpRoutes, Response, ResponseCookie}

import java.time.OffsetDateTime


class AuthenticationApi[F[_] : Async](encryptor: Encryptor, userService: UserService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case req@POST -> Root / "login" =>
        req.decode[Credentials] { user =>
          EitherT(userService.authenticate(user)).foldF(
            e => Applicative[F].pure(Response(Unauthorized).withEntity(e.msg)),
            session => {
              val secret = encryptor.encryptToken(s"${user.login}@@@${session.id}")
              val expiryDate = HttpDate.unsafeFromInstant(OffsetDateTime.now().plusHours(1).toInstant)
              Ok().map(
              _.addCookie(ResponseCookie("auth", secret, expires = Some(expiryDate), secure = false, httpOnly = true)))
            }
          )
        }
    }
  }
}