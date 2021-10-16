package service.spi

import domain.VirtualMachine

trait VirtualMachineService[F[_]] {
  def getMachines: F[Vector[VirtualMachine]]
  def startMachine(machineId: String): F[String]
  def stopMachine(machineId: String): F[String]
}