package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

object AwsS3Client {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, S3Client] =
    AwsClient[F, S3Client](awsSdkConfig, credentialsProvider =>
      S3Client
        .builder()
        .region(Region.US_EAST_1)
        .credentialsProvider(credentialsProvider)
        .build()
    )
}
