import cats.effect.{ExitCode, IO, IOApp}
import infrastructure.api.ObjectStorageApi
import infrastructure.aws.S3Client
import infrastructure.configuration.Config
import infrastructure.http.HttpServer

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    s3Client = S3Client[IO](config.aws)
    server <- s3Client.use { client =>
      val api = new ObjectStorageApi(client)
      HttpServer[IO](config.httpServer.host, config.httpServer.port, api.routes.orNotFound)
      .useForever
      .as(ExitCode.Success)
    }

  } yield server

}
