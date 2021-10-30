package domain.spi

import domain.model.{Credentials, User}

case class UserCreationError(msg: String) extends Throwable(msg)
case class UserFetchError(msg: String) extends Throwable(msg)
case class AuthenticationError(msg: String) extends Throwable(msg)

case class Session(id: String)

trait UserService[F[_]] {
  def createUser(user: User): F[Either[UserCreationError, Unit]]
  def getUserByLogin(login: String): F[Either[UserFetchError, Option[User]]]
  //def getUserBySessionId(sessionId: String): F[Either[UserFetchError, Option[User]]]
  def authenticate(credentials: Credentials): F[Either[AuthenticationError, Session]]
}
