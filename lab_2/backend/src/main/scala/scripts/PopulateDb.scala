package scripts

import cats.effect.{ExitCode, IO, IOApp}
import infrastructure.aws.client.DynamoDBClient
import infrastructure.aws.service.DynamoDbUserService
import infrastructure.configuration.Config

object PopulateDb extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    _ <- DynamoDBClient[IO](config.awsSdk).use { client =>
      val usersService = new DynamoDbUserService[IO](client)
      for {
        _ <- IO.println("Creating admin user")
        _ <- usersService.createUser(config.api.adminCredentials)
      } yield ()
    }
  } yield ExitCode.Success
}
