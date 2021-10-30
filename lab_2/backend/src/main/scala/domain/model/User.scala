package domain.model

case class User(login: String, password: String, sessionId: Option[String])

object User {
  def apply(login: String, password: String): User =
    User(login, password, None)
}

case class Credentials(login: String, password: String)


