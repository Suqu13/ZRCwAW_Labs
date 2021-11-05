package domain.spi

import domain.model.{AccessLog, User}
import org.http4s.{Method, Request, Response}

import java.time.Instant

case class AccessLogsFetchError(msg: String) extends Throwable(msg)

trait AccessLogsService[F[_]] {
  def log(request: Request[F], response: Response[F], user: Option[User]): F[Either[Throwable, AccessLog]]

  def fetchAccessLogs(
                       userLogin: Option[String],
                       method: Option[Method],
                       from: Option[Instant],
                       to: Option[Instant]
                     ): F[Either[Throwable, List[AccessLog]]]
}

