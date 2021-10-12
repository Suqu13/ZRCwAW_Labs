import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import infrastructure.api.ObjectStorageApi
import infrastructure.aws.AwsS3Client
import infrastructure.configuration.Config
import infrastructure.http.{HttpApi, HttpServer}
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.server.Router
import service.S3ObjectStorageService
import cats.syntax.semigroupk._

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    s3Client = AwsS3Client[IO](config.aws)
    server <- s3Client.use { client =>
      val s3Service = S3ObjectStorageService[IO](client)
      val s3Api = new ObjectStorageApi(s3Service)

      val routes = app(s3Api)

      HttpServer[IO](config.httpServer.host, config.httpServer.port, routes)
      .useForever
      .as(ExitCode.Success)
    }

  } yield server

  def app[F[_]: Monad](apiRoutes: HttpApi[F]*): HttpApp[F] =
    Router(
      "/api/v1" -> apiRoutes.map(_.routes).foldLeft(HttpRoutes.empty[F])(_ <+> _)
    ).orNotFound
}
