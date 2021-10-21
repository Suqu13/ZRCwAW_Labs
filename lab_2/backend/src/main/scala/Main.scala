import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.semigroupk._
import infrastructure.api.{ImageAnalysisApi, LanguageAnalysisApi, ObjectStorageApi, VirtualMachineApi}
import infrastructure.aws.client.{AwsComprehendClient, AwsEc2Client, AwsRekognitionClient, AwsS3AsyncClient, AwsS3Client}
import infrastructure.aws.service.{ComprehendLanguageAnalysisService, Ec2VirtualMachineService, RekognitionImageAnalysisService, S3ObjectStorageService}
import infrastructure.configuration.Config
import infrastructure.http.{HttpApi, HttpServer}
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    server <- (for {
      s3Client <- AwsS3Client[IO](config.aws)
      s3AsyncClient <- AwsS3AsyncClient[IO](config.aws)
      ec2Client <- AwsEc2Client[IO](config.aws)
      comprehendClient <- AwsComprehendClient[IO](config.aws)
      rekognitionClient <- AwsRekognitionClient[IO](config.aws)
    } yield (s3Client, s3AsyncClient, ec2Client, comprehendClient, rekognitionClient)) use {
      case (s3Client, s3AsyncClient, ec2Client, comprehendClient, rekognitionClient) =>
        val s3Service = S3ObjectStorageService[IO](s3Client, s3AsyncClient)
        val s3Api = new ObjectStorageApi[IO](s3Service)

        val ec2Service = Ec2VirtualMachineService[IO](ec2Client)
        val ec2Api = new VirtualMachineApi[IO](ec2Service)

        val comprehendService = new ComprehendLanguageAnalysisService[IO](comprehendClient)
        val comprehendApi = new LanguageAnalysisApi[IO](comprehendService)

        val rekognitionService = new RekognitionImageAnalysisService[IO](rekognitionClient)
        val rekognitionApi = new ImageAnalysisApi[IO](rekognitionService)

        val routes = app(s3Api.routes, ec2Api.routes, comprehendApi.routes, rekognitionApi.routes)

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
