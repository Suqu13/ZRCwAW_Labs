package infrastructure.api

import cats.effect.IO
import fs2.io.readInputStream
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._
import org.http4s.multipart.Multipart
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
    case GET -> "s3" /: storageName /: key =>
      objectStorageService.downloadObject(storageName, key.toString()).foldF(
        e => NotFound(e.getMessage),
        r => Ok(readInputStream(IO(r), 1024))
      )

    case req@POST -> "s3" /: storageName /: key =>
      req.decode[Multipart[IO]] { m =>
        val `object` = m.parts.head.body
        objectStorageService.uploadObject(storageName, key.toString(), `object`).foldF(
          e => NotFound(e.getMessage),
          response => Ok(response)
        )
      }
  }
}
