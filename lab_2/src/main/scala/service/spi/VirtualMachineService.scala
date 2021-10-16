package service.spi

import domain.VirtualMachine

trait VirtualMachineService[F[_]] {
  def getMachines: F[Vector[VirtualMachine]]
}