package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import cats.syntax.all._
import infrastructure.configuration.AwsConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.rekognition.RekognitionClient

object AwsRekognitionClient {
  def apply[F[_] : Sync : Console](awsConfig: AwsConfig): Resource[F, RekognitionClient] =
    Resource.make {
      val credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
        awsConfig.secretKeyId,
        awsConfig.secretAccessKey,
        awsConfig.sessionToken
      ))
      Sync[F].blocking(RekognitionClient.builder().region(Region.US_EAST_1).credentialsProvider(credentialsProvider).build())
    } { client =>
      Sync[F].blocking(client.close()).handleErrorWith(e => Console[F].println(e.getMessage))
    }
}
