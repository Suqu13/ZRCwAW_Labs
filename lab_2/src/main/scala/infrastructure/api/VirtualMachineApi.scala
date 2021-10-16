package infrastructure.api

import cats.Monad
import cats.effect.Async
import cats.implicits._
import infrastructure.http.HttpApi
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._
import service.spi.VirtualMachineService

class VirtualMachineApi[F[_] : Monad : Async](virtualMachineService: VirtualMachineService[F]) extends HttpApi[F] {

  val routes: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "ec2" =>
        virtualMachineService.getMachines.flatMap(Ok(_))
    }
  }
}
