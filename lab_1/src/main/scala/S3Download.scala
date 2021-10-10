import cats.effect.std.Console
import cats.effect.{ExitCode, IO, IOApp, Resource, Sync}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import cats.syntax.all._
import com.amazonaws.services.s3.model.S3Object

import java.io.{File, FileOutputStream, InputStream, OutputStream}

object S3Download extends IOApp {
  val bucketName = "413742098112-static-website-bucket"
  val objectKey = "index.html"

  override def run(args: List[String]): IO[ExitCode] = for {
    _ <- s3Client[IO].use { client =>
      for {
        s3Object <- objectRequest[IO](client, bucketName, objectKey)
        dest = new File(objectKey)
        bytes <- download[IO](s3Object, dest)
        _ <- IO.println(s"Downloaded $bytes bytes from $bucketName/$objectKey")
      } yield ()
    }
  } yield ExitCode.Success


  private def download[F[_]: Sync: Console](s3Object: S3Object, destination: File): F[Long] =
    streams(s3Object, destination).use { case (in, out) => transmit(in, out, new Array[Byte](1024 * 10), 0L) }

  private def s3Client[F[_]: Sync: Console]: Resource[F, AmazonS3] =
    Resource.make {
      Sync[F].blocking(AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build())
    }{ client =>
      Sync[F].blocking(client.shutdown()).handleErrorWith(e => Console[F].println(e.getMessage))
    }

  private def objectRequest[F[_]: Sync: Console](client: AmazonS3, bucketName: String, objectKey: String): F[S3Object] =
    Sync[F].blocking(client.getObject(bucketName, objectKey))

  private def inputStream[F[_]: Sync : Console](s3Object: S3Object): Resource[F, InputStream] =
    Resource.make {
      Sync[F].blocking(s3Object.getObjectContent)
    } { inStr =>
      Sync[F].blocking(inStr.close()).handleErrorWith(e => Console[F].println(e.getMessage))
    }

  private def outputStream[F[_]: Sync : Console](f: File): Resource[F, OutputStream] =
    Resource.make {
      Sync[F].blocking(new FileOutputStream(f))
    } { outStr =>
      Sync[F].blocking(outStr.close()).handleErrorWith(e => Console[F].println(e.getMessage))
    }

  private def streams[F[_]: Sync : Console](s3Object: S3Object, destination: File): Resource[F, (InputStream, OutputStream)] =
    for {
      inStr <- inputStream(s3Object)
      outStr <- outputStream(destination)
    } yield (inStr, outStr)


  private def transmit[F[_]: Sync](origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): F[Long] =
    for {
      amount <- Sync[F].blocking(origin.read(buffer, 0, buffer.length))
      count  <- if(amount > -1) Sync[F].blocking(destination.write(buffer, 0, amount)) >> transmit(origin, destination, buffer, acc + amount)
      else Sync[F].pure(acc)
    } yield count

}
