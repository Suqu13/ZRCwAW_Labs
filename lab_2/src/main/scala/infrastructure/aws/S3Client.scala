package infrastructure.aws

import cats.effect.{Resource, Sync}
import cats.effect.std.Console
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import infrastructure.configuration.AwsConfig
import cats.syntax.all._
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicSessionCredentials}


object S3Client {
  def apply[F[_]: Sync: Console](awsConfig: AwsConfig): Resource[F, AmazonS3] =
    Resource.make {
      val credentialsProvider = new AWSStaticCredentialsProvider(new BasicSessionCredentials(
        awsConfig.secretKeyId,
        awsConfig.secretAccessKey,
        awsConfig.sessionToken
      ))
      Sync[F].blocking(AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(credentialsProvider).build())
    }{ client =>
      Sync[F].blocking(client.shutdown()).handleErrorWith(e => Console[F].println(e.getMessage))
    }
}
