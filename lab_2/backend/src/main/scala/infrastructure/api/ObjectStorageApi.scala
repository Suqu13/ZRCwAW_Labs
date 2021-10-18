package infrastructure.api

import cats.Monad
import cats.effect.Async
import cats.implicits._
import fs2.io.readInputStream
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.{HttpRoutes, Response, Status}
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._
import org.http4s.multipart.Multipart
import domain.spi.ObjectStorageService

class ObjectStorageApi[F[_]: Monad : Async](objectStorageService: ObjectStorageService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
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
          r => Ok(readInputStream[F](Monad[F].pure(r), 1024))
        )
      case DELETE -> "s3" /: storageName /: key =>
        objectStorageService.deleteObject(storageName, key.toString()).foldF(
          e => NotFound(e.getMessage),
          s => Monad[F].pure(Response(Status.fromInt(s).getOrElse(InternalServerError)))
        )
      case req@POST -> "s3" /: storageName /: key =>
        req.decode[Multipart[F]] { m =>
          val `object` = m.parts.head.body
          objectStorageService.uploadObject(storageName, key.toString(), `object`).foldF(
            e => NotFound(e.getMessage),
            response => Ok(response)
          )
        }
    }
  }
}
