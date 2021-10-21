package domain.spi

import domain.model.VirtualMachine

trait VirtualMachineService[F[_]] {
  def getMachines: F[Vector[VirtualMachine]]
  def getMachine(machineId: String): F[VirtualMachine]
  def startMachine(machineId: String): F[String]
  def stopMachine(machineId: String): F[String]
}
