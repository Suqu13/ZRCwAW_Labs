import cats.Monad
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.semigroupk._
import domain.model.User
import infrastructure.api._
import infrastructure.aws.client._
import infrastructure.aws.service._
import infrastructure.configuration.Config
import infrastructure.http.HttpServer
import infrastructure.security.Encryptor
import org.http4s.server.{AuthMiddleware, Router}
import org.http4s.{AuthedRoutes, HttpApp, HttpRoutes}

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
      rekognitionClient <- AwsRekognitionClient[IO](config.awsSdk)
      dynamoDbClient <- DynamoDBClient[IO](config.awsSdk)
    } yield (
      s3Client,
      s3AsyncClient,
      ec2Client,
      comprehendClient,
      pollyAsyncClient,
      translateAsyncClient,
      rekognitionClient,
      dynamoDbClient
    )) use {
      case (
        s3Client,
        s3AsyncClient,
        ec2Client,
        comprehendClient,
        pollyAsyncClient,
        translateAsyncClient,
        rekognitionClient,
        dynamoDBClient
        ) =>
        val s3Service = S3ObjectStorageService[IO](s3Client, s3AsyncClient)
        val s3Api = new ObjectStorageApi[IO](s3Service)

        val ec2Service = Ec2VirtualMachineService[IO](ec2Client)
        val ec2Api = new VirtualMachineApi[IO](ec2Service)

        val comprehendService = new ComprehendLanguageAnalysisService[IO](comprehendClient)
        val comprehendApi = new LanguageAnalysisApi[IO](comprehendService)

        val readService = new AwsPollyService[IO](pollyAsyncClient)
        val readApi = new ReadApi[IO](readService)

        val translateService = new AwsTranslateService[IO](translateAsyncClient)
        val translateApi = new TranslateApi[IO](translateService)

        val rekognitionService = new RekognitionImageAnalysisService[IO](rekognitionClient)
        val rekognitionApi = new ImageAnalysisApi[IO](rekognitionService)

        val usersService = new DynamoDbUserService[IO](dynamoDBClient)
        val encryptor = new Encryptor(config.encryption.key)
        val authenticationApi = new AuthenticationApi[IO](encryptor, usersService)
        val basicAuth = BasicAuth(usersService, encryptor)

        val routes = app(authenticationApi.routes)(
          basicAuth,
          s3Api.routes,
          ec2Api.routes,
          comprehendApi.routes,
          readApi.routes,
          translateApi.routes,
          rekognitionApi.routes
        )

        HttpServer[IO](config.httpServer.host, config.httpServer.port, routes)
          .useForever
          .as(ExitCode.Success)
    }

  } yield server

  def app[F[_] : Monad](routes: HttpRoutes[F]*)(
    authProvider: AuthMiddleware[F, User],
    authedRoutes: AuthedRoutes[User, F]*
  ): HttpApp[F] =
    Router(
      "/api/v1" -> routes.foldLeft(HttpRoutes.empty[F])(_ <+> _)
        .combineK(authProvider(authedRoutes.foldLeft(AuthedRoutes.empty[User, F])(_ <+> _)))
    ).orNotFound
}
