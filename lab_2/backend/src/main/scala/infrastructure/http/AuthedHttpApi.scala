package infrastructure.http

import org.http4s.AuthedRoutes

trait AuthedHttpApi[F[_], T] {
  def routes: AuthedRoutes[T, F]
}
