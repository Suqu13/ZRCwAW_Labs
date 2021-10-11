package infrastructure.http

import cats.effect.kernel.{Async, Resource}
import org.http4s.HttpApp
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Server

object HttpServer {
  def instance[F[_]: Async](host: String, port: Int, app: HttpApp[F]): Resource[F, Server] =
    BlazeServerBuilder[F]
      .bindHttp(port, host)
      .withHttpApp(app)
      .resource
}
