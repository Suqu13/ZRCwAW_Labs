package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ec2.Ec2Client
import cats.syntax.all._

object AwsEc2Client {
  def apply[F[_] : Sync : Console](awsSdkConfig: AwsSdkConfig): Resource[F, Ec2Client] =
    AwsClient[F, Ec2Client](awsSdkConfig, credentialsProvider =>
      Ec2Client
        .builder()
        .credentialsProvider(credentialsProvider)
        .region(Region.US_EAST_1)
        .build()
    )
}
