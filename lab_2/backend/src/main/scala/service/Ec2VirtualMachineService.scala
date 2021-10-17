package service

import cats.effect.Async
import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.{Applicative, Functor}
import domain.VirtualMachine
import service.spi.VirtualMachineService
import software.amazon.awssdk.services.ec2.Ec2Client
import software.amazon.awssdk.services.ec2.model.{DescribeInstancesRequest, StartInstancesRequest, StopInstancesRequest}

import scala.jdk.CollectionConverters.ListHasAsScala

class Ec2VirtualMachineService[F[_] : Functor : Async : Console : Applicative](ec2Client: Ec2Client) extends VirtualMachineService[F] {
  override def getMachines: F[Vector[VirtualMachine]] = {
    val maxResults = 100
    val nextToken: String = null
    val request = DescribeInstancesRequest.builder.maxResults(maxResults).nextToken(nextToken).build
    val response = ec2Client.describeInstances(request)

    val instances = response.reservations.asScala
      .flatMap(reservation => reservation.instances().asScala)
      .map(instance => VirtualMachine(instance.instanceId, instance.state.name.toString))
      .toVector

    Sync[F].blocking(instances)
  }


  override def getMachine(machineId: String): F[VirtualMachine] = {
    val maxResults = 100
    val nextToken: String = null
    val request = DescribeInstancesRequest.builder.maxResults(maxResults).nextToken(nextToken).build
    val response = ec2Client.describeInstances(request)

    val instance = response.reservations.asScala
      .flatMap(reservation => reservation.instances().asScala)
      .map(instance => VirtualMachine(instance.instanceId, instance.state.name.toString))
      .filter(instance => instance.instanceId.equals(machineId))
      .head

    Sync[F].blocking(instance)
  }

  override def startMachine(machineId: String): F[String] = {
    val request = StartInstancesRequest.builder.instanceIds(machineId).build
    val resp = ec2Client.startInstances(request)
    Sync[F].blocking(resp.startingInstances().asScala.head.currentState().name().toString)
  }

  override def stopMachine(machineId: String): F[String] = {
    val request = StopInstancesRequest.builder.instanceIds(machineId).build
    val resp = ec2Client.stopInstances(request)
    Sync[F].blocking(resp.stoppingInstances().asScala.head.currentState().name().toString)
  }
}

object Ec2VirtualMachineService {
  def apply[F[_] : Functor : Async : Console](ec2Client: Ec2Client): Ec2VirtualMachineService[F] =
    new Ec2VirtualMachineService(ec2Client)
}