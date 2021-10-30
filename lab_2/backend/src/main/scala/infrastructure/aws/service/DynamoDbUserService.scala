package infrastructure.aws.service

import cats.Applicative
import cats.effect.kernel.Sync
import cats.syntax.all._
import domain.model.{Credentials, User}
import domain.spi.{AuthenticationError, Session, UserCreationError, UserFetchError, UserService}
import org.scanamo.generic.auto._
import org.scanamo.syntax._
import org.scanamo.{DynamoReadError, Scanamo, Table}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import java.util.UUID

class DynamoDbUserService[F[_] : Sync](dynamoDbClient: DynamoDbClient) extends UserService[F] {

  private lazy val dbExecutor = Scanamo(dynamoDbClient)
  private lazy val usersTable = Table[User]("Users")

  override def createUser(user: User): F[Either[UserCreationError, Unit]] = for {
    queryRes <- Sync[F].blocking(dbExecutor.exec(usersTable.scan()))
    users <- queryRes.collect {
      case Right(user) => Applicative[F].pure(user)
    }.sequence
    putRes <- if (users.exists(_.login.equals(user.login))) {
      Applicative[F].pure(Either.left(UserCreationError("User with given login already exists.")))
    } else {
      Sync[F].blocking(dbExecutor.exec(usersTable.put(user))).map(Either.right)
    }
  } yield putRes

  override def getUserByLogin(login: String): F[Either[UserFetchError, Option[User]]] = for {
    queryRes <- Sync[F].blocking(dbExecutor.exec(usersTable.get("login" === login)))
    res = queryRes match {
      case Some(value) => value match {
        case Left(_) => Either.left(UserFetchError("Unable to fetch user"))
        case Right(user) => Either.right(Some(user))
      }
      case None => Either.right(Option.empty[User])
    }
  } yield res

  override def authenticate(credentials: Credentials): F[Either[AuthenticationError, Session]] = for {
    dbQuery <- getUserByLogin(credentials.login)
    res <- dbQuery.fold(
      e => Applicative[F].pure(Either.left(AuthenticationError(e.msg))),
      {
        case Some(dbUser) if dbUser.password == credentials.password =>
          updateSessionId(dbUser).map(_.leftMap(_ => AuthenticationError("Unable to find user")))
        case Some(dbUser) =>
          Applicative[F].pure(Either.left(AuthenticationError(s"Invalid password for ${dbUser.login}")))
        case None =>
          Applicative[F].pure(Either.left(AuthenticationError(s"User ${credentials.login} does not exist")))
      }
    )
  } yield res

  private def updateSessionId(user: User): F[Either[DynamoReadError, Session]] = {
    val sessionId = UUID.randomUUID().toString
    for {
      res <- Sync[F].blocking(
        dbExecutor.exec(usersTable.update("login" === user.login, set("sessionId", Some(sessionId))))
      )
    } yield res.map(_ => Session(sessionId))
  }
}
