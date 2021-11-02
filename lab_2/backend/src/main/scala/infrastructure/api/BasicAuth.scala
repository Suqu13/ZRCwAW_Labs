package infrastructure.api

import cats.Monad
import cats.data.{EitherT, Kleisli, OptionT}
import cats.syntax.all._
import domain.model.User
import domain.spi.{AuthenticationError, UserService}
import infrastructure.security.Encryptor
import org.http4s.Status.Unauthorized
import org.http4s.headers.Cookie
import org.http4s.server.AuthMiddleware
import org.http4s.{AuthedRoutes, Request, Response}

object BasicAuth {
  def apply[F[_]: Monad](userService: UserService[F], encryptor: Encryptor): AuthMiddleware[F, User] =
    AuthMiddleware[F, AuthenticationError, User](authUser(userService, encryptor), onFailure)

  private def onFailure[F[_]: Monad]: AuthedRoutes[AuthenticationError, F] = Kleisli(r => OptionT.liftF(
    Monad[F].pure(Response(Unauthorized).withEntity(r.context.msg))))

  private def authUser[F[_]: Monad](userService: UserService[F], encryptor: Encryptor): Kleisli[F, Request[F], Either[AuthenticationError, User]] = Kleisli({ req =>
    val cookieContent = for {
      header <- req.headers.get[Cookie].toRight(AuthenticationError("Authentication data not provided"))
      cookie <- header.values.toList.find(_.name == "auth").toRight(AuthenticationError("Authentication data not provided"))
      res <- encryptor.decryptToken(cookie.content).toRight(AuthenticationError("Malformed authentication data"))
    } yield res
    cookieContent match {
      case Left(e) => Monad[F].pure(Either.left(e))
      case Right(content) =>
        val authData = content.split("@@@").toList
        if (authData.length == 2) {
          val (login :: sessionId :: Nil) = authData
          EitherT(userService.getUserByLogin(login))
            .foldF(
              e => Monad[F].pure(Either.left(AuthenticationError(e.msg))),
              {
                case Some(user) if user.sessionId.contains(sessionId) => Monad[F].pure(Either.right(user))
                case _ => Monad[F].pure(Either.left(AuthenticationError("Invalid sessionId")))
              }
            )
        } else Monad[F].pure(Either.left(AuthenticationError("Malformed authentication data")))
    }
  })
}
