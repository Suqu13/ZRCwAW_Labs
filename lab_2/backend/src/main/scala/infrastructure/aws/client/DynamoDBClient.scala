package infrastructure.aws.client

import cats.effect.{Resource, Sync}
import cats.effect.std.Console
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

object DynamoDBClient {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, DynamoDbClient] =
    AwsClient[F, DynamoDbClient](awsSdkConfig, credentialsProvider =>
      DynamoDbClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build()
    )
}
