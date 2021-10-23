package infrastructure.aws.client

import cats.effect.{Resource, Sync}
import cats.effect.std.Console
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.comprehend.ComprehendClient
import cats.syntax.all._

object AwsComprehendClient {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, ComprehendClient] =
    AwsClient[F, ComprehendClient](awsSdkConfig, credentialsProvider =>
      ComprehendClient
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build()
    )
}
