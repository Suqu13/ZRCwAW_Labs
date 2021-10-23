package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.polly.PollyAsyncClient

object AwsPollyAsyncClient {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, PollyAsyncClient] =
    AwsClient[F, PollyAsyncClient](awsSdkConfig, credentialsProvider =>
      PollyAsyncClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build()
    )
}
