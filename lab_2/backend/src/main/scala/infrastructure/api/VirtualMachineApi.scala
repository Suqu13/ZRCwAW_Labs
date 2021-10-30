package infrastructure.api

import cats.effect.Async
import cats.implicits._
import domain.model.User
import domain.spi.VirtualMachineService
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.AuthedRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._

class VirtualMachineApi[F[_] : Async](virtualMachineService: VirtualMachineService[F]) extends AuthedHttpApi[F, User] {

  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    AuthedRoutes.of[User, F] {
      case GET -> Root / "ec2" as _ =>
        virtualMachineService.getMachines.flatMap(Ok(_))
      case GET -> Root / "ec2" / machineId as _ =>
        virtualMachineService.getMachine(machineId).flatMap(Ok(_))
      case GET -> Root / "ec2" / machineId / "start" as _ =>
        virtualMachineService.startMachine(machineId).flatMap(Ok(_))
      case GET -> Root / "ec2" / machineId / "stop" as _ =>
        virtualMachineService.stopMachine(machineId).flatMap(Ok(_))
    }
  }
}
