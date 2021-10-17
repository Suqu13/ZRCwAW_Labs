import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.semigroupk._
import infrastructure.api.{ObjectStorageApi, VirtualMachineApi}
import infrastructure.aws.{AwsEc2Client, AwsS3AsyncClient, AwsS3Client}
import infrastructure.configuration.Config
import infrastructure.http.{HttpApi, HttpServer}
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}
import service.{Ec2VirtualMachineService, S3ObjectStorageService}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    server <- (for {
      s3Client <- AwsS3Client[IO](config.aws)
      s3AsyncClient <- AwsS3AsyncClient[IO](config.aws)
      ec2Client <- AwsEc2Client[IO](config.aws)
    } yield (s3Client, s3AsyncClient, ec2Client)) use {
      case (s3Client, s3AsyncClient, ec2Client) =>
        val s3Service = S3ObjectStorageService[IO](s3Client, s3AsyncClient)
        val s3Api = new ObjectStorageApi[IO](s3Service)

        val ec2Service = Ec2VirtualMachineService[IO](ec2Client)
        val ec2Api = new VirtualMachineApi[IO](ec2Service)

        val mergedApiRoutes = s3Api.routes <+> ec2Api.routes
        val routes = app(mergedApiRoutes)

        HttpServer[IO](config.httpServer.host, config.httpServer.port, routes)
          .useForever
          .as(ExitCode.Success)
    }

  } yield server

  def app[F[_] : Monad](apiRoutes: HttpRoutes[F]*): HttpApp[F] =
    Router(
      "/api/v1" -> apiRoutes.foldLeft(HttpRoutes.empty[F])(_ <+> _)
    ).orNotFound
}
