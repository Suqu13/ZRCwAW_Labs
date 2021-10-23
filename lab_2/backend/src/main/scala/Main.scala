import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.semigroupk._
import infrastructure.api.{LanguageAnalysisApi, ObjectStorageApi, ReadApi, TranslateApi, VirtualMachineApi}
import infrastructure.aws.client.{AwsComprehendClient, AwsEc2Client, AwsPollyAsyncClient, AwsS3AsyncClient, AwsS3Client, AwsTranslateAsyncClient}
import infrastructure.aws.service.{AwsPollyService, AwsTranslateService, ComprehendLanguageAnalysisService, Ec2VirtualMachineService, S3ObjectStorageService}
import infrastructure.configuration.Config
import infrastructure.http.{HttpApi, HttpServer}
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    server <- (for {
      s3Client <- AwsS3Client[IO](config.awsSdk)
      s3AsyncClient <- AwsS3AsyncClient[IO](config.awsSdk)
      ec2Client <- AwsEc2Client[IO](config.awsSdk)
      comprehendClient <- AwsComprehendClient[IO](config.awsSdk)
      pollyAsyncClient <- AwsPollyAsyncClient[IO](config.awsSdk)
      translateAsyncClient <- AwsTranslateAsyncClient[IO](config.awsSdk)
    } yield (s3Client, s3AsyncClient, ec2Client, comprehendClient, pollyAsyncClient, translateAsyncClient)) use {
      case (s3Client, s3AsyncClient, ec2Client, comprehendClient, pollyAsyncClient, translateAsyncClient) =>
        val s3Service = S3ObjectStorageService[IO](s3Client, s3AsyncClient)
        val s3Api = new ObjectStorageApi[IO](s3Service)

        val ec2Service = Ec2VirtualMachineService[IO](ec2Client)
        val ec2Api = new VirtualMachineApi[IO](ec2Service)

        val comprehendService = new ComprehendLanguageAnalysisService[IO](comprehendClient)
        val comprehendApi = new LanguageAnalysisApi[IO](comprehendService)

        val readService = new AwsPollyService[IO](pollyAsyncClient)
        val readApi= new ReadApi[IO](readService)

        val translateService = new AwsTranslateService[IO](translateAsyncClient)
        val translateApi = new TranslateApi[IO](translateService)


        val routes = app(s3Api.routes, ec2Api.routes, comprehendApi.routes, readApi.routes, translateApi.routes)

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
