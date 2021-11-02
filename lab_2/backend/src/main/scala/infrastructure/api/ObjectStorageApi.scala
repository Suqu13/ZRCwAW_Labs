package infrastructure.api

import cats.Monad
import cats.effect.Async
import cats.implicits._
import domain.model.User
import domain.spi.ObjectStorageService
import fs2.io.readInputStream
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._
import org.http4s.multipart.Multipart
import org.http4s.{AuthedRoutes, Response, Status}

class ObjectStorageApi[F[_]: Monad : Async](objectStorageService: ObjectStorageService[F]) extends AuthedHttpApi[F, User] {

  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of[User, F] {
      case GET -> Root / "s3" as _ =>
        objectStorageService.getStorages.flatMap(Ok(_))
      case GET -> Root / "s3" / storageName as _ =>
        objectStorageService.listStorage(storageName).foldF(
          e => NotFound(e.getMessage),
          Ok(_)
        )
      case GET -> "s3" /: storageName /: key as _ =>
        objectStorageService.downloadObject(storageName, key.toString()).foldF(
          e => NotFound(e.getMessage),
          r => Ok(readInputStream[F](Monad[F].pure(r), 1024))
        )
      case DELETE -> "s3" /: storageName /: key as _ =>
        objectStorageService.deleteObject(storageName, key.toString()).foldF(
          e => NotFound(e.getMessage),
          s => Monad[F].pure(Response(Status.fromInt(s).getOrElse(InternalServerError)))
        )
      case authReq@POST -> "s3" /: storageName /: key as _ =>
        authReq.req.decode[Multipart[F]] { m =>
          val `object` = m.parts.head.body
          objectStorageService.uploadObject(storageName, key.toString(), `object`).foldF(
            e => NotFound(e.getMessage),
            response => Ok(response)
          )
        }
    }
  }
}
