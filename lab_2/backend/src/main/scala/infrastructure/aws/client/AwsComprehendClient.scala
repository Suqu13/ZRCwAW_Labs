package infrastructure.aws.client

import cats.effect.{Resource, Sync}
import cats.effect.std.Console
import infrastructure.configuration.AwsConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.comprehend.ComprehendClient
import cats.syntax.all._

object AwsComprehendClient {
  def apply[F[_] : Sync : Console](awsConfig: AwsConfig): Resource[F, ComprehendClient] =
    Resource.make {
      val credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
        awsConfig.secretKeyId,
        awsConfig.secretAccessKey,
        awsConfig.sessionToken
      ))
      Sync[F].blocking(ComprehendClient.builder().region(Region.US_EAST_1).credentialsProvider(credentialsProvider).build())
    } { client =>
      Sync[F].blocking(client.close()).handleErrorWith(e => Console[F].println(e.getMessage))
    }
}
