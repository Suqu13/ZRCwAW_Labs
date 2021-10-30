import cats.effect.{ExitCode, IO, IOApp}
import infrastructure.aws.client.DynamoDBClient
import infrastructure.aws.service.DynamoDbUserService
import infrastructure.configuration.Config
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model._

import scala.concurrent.duration.DurationInt


object DBSetup extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    _ <- DynamoDBClient[IO](config.awsSdk).use { client =>
      val usersService = new DynamoDbUserService[IO](client)
      for {
        _ <- createTable(client, "login", "Users")
        _ <- IO.sleep(10 seconds)
        _ <- IO.println("Creating admin user")
        _ <- usersService.createUser(config.api.adminUser)
      } yield ()
    }
  } yield ExitCode.Success


  def createTable(client: DynamoDbClient, key: String, tableName: String): IO[CreateTableResponse] = {
    val req = CreateTableRequest
      .builder()
      .attributeDefinitions(AttributeDefinition.builder()
        .attributeName(key)
        .attributeType(ScalarAttributeType.S)
        .build())
      .keySchema(KeySchemaElement.builder()
        .attributeName(key)
        .keyType(KeyType.HASH)
        .build())
      .provisionedThroughput(ProvisionedThroughput.builder()
        .readCapacityUnits(10)
        .writeCapacityUnits(10)
        .build())
      .tableName(tableName)
      .build()
    IO.blocking(client.createTable(req))
  }
}
