package infrastructure.api

import cats.effect.IO
import fs2.io.readInputStream
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import service.S3ObjectStorageService
import service.spi.ObjectStorageService

class ObjectStorageApi(objectStorageService: ObjectStorageService[IO]) extends HttpApi[IO] {

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "s3" =>
      objectStorageService.getStorages.flatMap(Ok(_))
    case GET -> Root / "s3" / storageName =>
      objectStorageService.listStorage(storageName).foldF(
        e => NotFound(e.getMessage),
        Ok(_)
      )
    case GET -> Root / "s3" / storageName / objectKey =>
      objectStorageService.downloadObject(storageName, objectKey).foldF(
        e => NotFound(e.getMessage),
        r => Ok(readInputStream(IO(r), 1024))
      )

  }
}
