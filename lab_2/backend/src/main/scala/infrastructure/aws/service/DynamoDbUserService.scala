package infrastructure.aws.service

import cats.Applicative
import cats.effect.kernel.Sync
import cats.syntax.all._
import domain.model.User
import domain.spi.{UserCreationError, UserFetchError, UserService}
import org.scanamo.generic.auto._
import org.scanamo.syntax._
import org.scanamo.{Scanamo, Table}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class UserServiceImpl[F[_] : Sync](dynamoDbClient: DynamoDbClient) extends UserService[F] {

  private lazy val scanamoClient = Scanamo(dynamoDbClient)
  private lazy val usersTable = Table[User]("Users")

  override def createUser(user: User): F[Either[UserCreationError, Unit]] = for {
    queryRes <- Sync[F].blocking(scanamoClient.exec(usersTable.scan()))
    users <- queryRes.collect {
      case Right(user) => Applicative[F].pure(user)
    }.sequence
    putRes <- if (users.exists(_.login.equals(user.login))) {
      Applicative[F].pure(Either.left(UserCreationError("User with given login already exists.")))
    } else {
      Sync[F].blocking(scanamoClient.exec(usersTable.put(user))).map(Either.right)
    }
  } yield putRes

  override def getUserByLogin(login: String): F[Either[UserFetchError, Option[User]]] = for {
    queryRes <- Sync[F].blocking(scanamoClient.exec(usersTable.get("login" === login)))
    res = queryRes match {
      case Some(value) => value match {
        case Left(_) => Either.left(UserFetchError("Unable to fetch user"))
        case Right(user) => Either.right(Some(user))
      }
      case None => Either.right(Option.empty[User])
    }
  } yield res
}
