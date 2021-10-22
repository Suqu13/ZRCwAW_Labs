package infrastructure.aws.client

import cats.effect.std.Console
import cats.effect.{Resource, Sync}
import cats.implicits.catsSyntaxApplicativeError
import infrastructure.configuration.AwsSdkConfig
import software.amazon.awssdk.auth.credentials.{AwsSessionCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.SdkClient

private[client] object AwsClient {
  def apply[F[_] : Sync : Console, T <: SdkClient](awsSdkConfig: AwsSdkConfig, createSdkClient: StaticCredentialsProvider => T): Resource[F, T] =
    Resource.make {
      val credentialsProvider = StaticCredentialsProvider.create(AwsSessionCredentials.create(
        awsSdkConfig.secretKeyId,
        awsSdkConfig.secretAccessKey,
        awsSdkConfig.sessionToken
      ))
      Sync[F].blocking(createSdkClient(credentialsProvider))
    } { client =>
      Sync[F].blocking(client.close())
        .handleErrorWith(e =>
          Console[F].println(e.getMessage)
        )
    }
}

