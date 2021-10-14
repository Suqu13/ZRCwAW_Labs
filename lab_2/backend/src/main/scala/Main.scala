import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.semigroupk._
import infrastructure.api.ObjectStorageApi
import infrastructure.aws.{AwsS3AsyncClient, AwsS3Client}
import infrastructure.configuration.Config
import infrastructure.http.{HttpApi, HttpServer}
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}
import service.S3ObjectStorageService

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    server <- (for {
      s3Client <- AwsS3Client[IO](config.aws)
      s3AsyncClient <- AwsS3AsyncClient[IO](config.aws)
    } yield (s3Client, s3AsyncClient)) use {
      case (s3Client, s3AsyncClient) =>
        val s3Service = S3ObjectStorageService[IO](s3Client, s3AsyncClient)
        val s3Api = new ObjectStorageApi[IO](s3Service)

        val routes = app(s3Api)

        HttpServer[IO](config.httpServer.host, config.httpServer.port, routes)
          .useForever
          .as(ExitCode.Success)
    }

  } yield server

  def app[F[_] : Monad](apiRoutes: HttpApi[F]*): HttpApp[F] =
    Router(
      "/api/v1" -> apiRoutes.map(_.routes).foldLeft(HttpRoutes.empty[F])(_ <+> _)
    ).orNotFound
}
