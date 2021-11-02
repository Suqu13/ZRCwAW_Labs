package domain.spi

import domain.model.User

case class UserCreationError(msg: String) extends Throwable(msg)
case class UserFetchError(msg: String) extends Throwable(msg)
case class AuthenticationError(msg: String) extends Throwable(msg)

trait UserService[F[_]] {
  def createUser(credentials: User.Credentials): F[Either[UserCreationError, Unit]]
  def getUserByLogin(login: String): F[Either[UserFetchError, Option[User]]]
  def authenticate(credentials: User.Credentials): F[Either[AuthenticationError, User.SessionId]]
}
