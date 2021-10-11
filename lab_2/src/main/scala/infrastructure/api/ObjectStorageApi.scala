package infrastructure.api

import cats.effect.IO
import com.amazonaws.services.s3.AmazonS3
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import service.S3ObjectStorageService
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

class ObjectStorageApi(s3Client: AmazonS3) {

  lazy val s3Service = new S3ObjectStorageService[IO](s3Client)

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "s3" / "buckets" =>
      s3Service.getStorages.flatMap(Ok(_))
  }
}
