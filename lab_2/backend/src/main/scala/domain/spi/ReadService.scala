package domain.spi

import domain.model.ReadText

trait ReadService[F[_]] {
  def read(input: ReadText.Input): F[Either[Throwable, ReadText.Output[F]]]
}
//
//sealed trait ReadServiceException extends Exception
//sealed case class UnknownException(message: String, throwable: Throwable) extends ReadServiceException
//sealed case class UnknownException(message: String, throwable: Throwable) extends ReadServiceException
//

