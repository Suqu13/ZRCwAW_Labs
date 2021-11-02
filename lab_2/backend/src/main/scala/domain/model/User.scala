package domain.model

case class User(login: String, password: String, sessionId: Option[String])

object User {
  type SessionId = String
  case class Credentials(login: String, password: String)

  def apply(credentials: Credentials): User =
    User(credentials.login, credentials.password, None)
}




