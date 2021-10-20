package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import infrastructure.configuration.AwsConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import cats.syntax.all._

object AwsS3AsyncClient {
  def apply[F[_] : Sync : Console](awsConfig: AwsConfig): Resource[F, S3AsyncClient] =
    Resource.make {
      val credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
        awsConfig.secretKeyId,
        awsConfig.secretAccessKey,
        awsConfig.sessionToken
      ))
      Sync[F].blocking(S3AsyncClient.builder().region(Region.US_EAST_1).credentialsProvider(credentialsProvider).build())
    } { client =>
      Sync[F].blocking(client.close()).handleErrorWith(e => Console[F].println(e.getMessage))
    }
}