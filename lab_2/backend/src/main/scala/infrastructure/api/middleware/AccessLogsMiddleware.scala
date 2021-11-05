package infrastructure.api.middleware

import cats.data.{Kleisli, OptionT}
import cats.effect._
import cats.effect.implicits.genSpawnOps
import cats.effect.std.Console
import cats.syntax.all._
import domain.model.User
import domain.spi.AccessLogsService
import org.http4s._

class AccessLogsMiddleware[F[_] : Async : Console](accessLogsService: AccessLogsService[F]) {
  def wrapAuthedRoutes(service: AuthedRoutes[User, F]): AuthedRoutes[User, F] = Kleisli { req: AuthedRequest[F, User] =>
    OptionT(for {
      res <- service(req).value
      _ <- res match {
        case Some(value) => accessLogsService.log(req.req, value, Some(req.context)).start
        case None => Async[F].unit
      }
    } yield res)
  }

  def wrapRoutes(service: HttpRoutes[F]): HttpRoutes[F] = Kleisli { req: Request[F] =>
    OptionT(for {
      res <- service(req).value
      _ <- res match {
        case Some(value) => accessLogsService.log(req, value, None).start
        case None => Async[F].unit
      }
    } yield res)
  }
}

