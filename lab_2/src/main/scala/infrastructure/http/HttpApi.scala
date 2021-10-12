package infrastructure.http

import org.http4s.HttpRoutes

trait HttpApi[F[_]] {
  def routes: HttpRoutes[F]
}
