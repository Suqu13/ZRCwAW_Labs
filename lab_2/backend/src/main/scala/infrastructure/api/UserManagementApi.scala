package infrastructure.api

import cats.Monad
import cats.data.EitherT
import cats.effect.Async
import domain.model.User
import domain.spi.UserService
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.dsl._

class UserManagementApi[F[_] : Monad : Async](userService: UserService[F]) extends AuthedHttpApi[F, User] {

  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of[User, F] {
      case authReq@POST -> Root / "user" as _ =>
        authReq.req.decode[User.Credentials] { creds =>
          EitherT(userService.createUser(creds))
            .foldF(
              e => BadRequest(e.msg),
              _ => Created()
            )
        }
    }
  }
}