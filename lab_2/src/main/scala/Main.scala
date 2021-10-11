import cats.effect.{ExitCode, IO, IOApp}
import infrastructure.configuration.Config
import infrastructure.http.HttpServer
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object Main extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  override def run(args: List[String]): IO[ExitCode] = for {
    config <- Config.load[IO]
    server <- HttpServer
      .instance[IO](config.httpServer.host, config.httpServer.port, helloWorldService)
      .use(_ => IO.never)
      .as(ExitCode.Success)
  } yield server

}
