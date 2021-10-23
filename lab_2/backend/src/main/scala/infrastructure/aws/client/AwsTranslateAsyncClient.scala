package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.translate.TranslateAsyncClient

object AwsTranslateAsyncClient {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, TranslateAsyncClient] =
    AwsClient[F, TranslateAsyncClient](awsSdkConfig, credentialsProvider =>
      TranslateAsyncClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build()
    )
}
