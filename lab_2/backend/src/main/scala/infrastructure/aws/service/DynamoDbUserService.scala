package infrastructure.aws.service

import cats.effect.kernel.Sync
import cats.syntax.all._
import domain.model.User
import domain.spi._
import org.scanamo.generic.auto._
import org.scanamo.syntax._
import org.scanamo.{DynamoReadError, Scanamo, Table}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import java.util.UUID

class DynamoDbUserService[F[_] : Sync](dynamoDbClient: DynamoDbClient) extends UserService[F] {

  private lazy val dbExecutor = Scanamo(dynamoDbClient)
  private lazy val usersTable = Table[User]("Users")

  override def createUser(credentials: User.Credentials): F[Either[UserCreationError, Unit]] = for {
    userQuery <- getUserByLogin(credentials.login)
    putRes <- userQuery.fold(
      e => Sync[F].pure(Either.left(UserCreationError(e.msg))),
      _.fold[F[Either[UserCreationError, Unit]]](
        Sync[F].blocking(dbExecutor.exec(usersTable.put(User(credentials)))).map(Either.right)
      )(_ =>
        Sync[F].pure(Either.left(UserCreationError("Invalid data provided.")))
      )
    )
  } yield putRes

  override def getUserByLogin(login: String): F[Either[UserFetchError, Option[User]]] = for {
    userQuery <- Sync[F].blocking(dbExecutor.exec(usersTable.get("login" === login)))
    res = userQuery match {
      case Some(value) => value match {
        case Left(_) => Either.left(UserFetchError("Unable to fetch user"))
        case Right(user) => Either.right(Some(user))
      }
      case None => Either.right(Option.empty[User])
    }
  } yield res

  override def authenticate(credentials: User.Credentials): F[Either[AuthenticationError, User.SessionId]] = for {
    userQuery <- getUserByLogin(credentials.login)
    res <- userQuery.fold(
      e => Sync[F].pure(Either.left(AuthenticationError(e.msg))),
      {
        case Some(dbUser) if dbUser.password == credentials.password =>
          updateSessionId(dbUser).map(_.leftMap(_ => AuthenticationError("Unable to find user")))
        case Some(dbUser) =>
          Sync[F].pure(Either.left(AuthenticationError(s"Invalid password for ${dbUser.login}")))
        case None =>
          Sync[F].pure(Either.left(AuthenticationError(s"User ${credentials.login} does not exist")))
      }
    )
  } yield res

  private def updateSessionId(user: User): F[Either[DynamoReadError, User.SessionId]] = {
    val sessionId = UUID.randomUUID().toString
    for {
      res <- Sync[F].blocking(
        dbExecutor.exec(usersTable.update("login" === user.login, set("sessionId", Some(sessionId))))
      )
    } yield res.map(_ => sessionId)
  }
}
