package service.spi

import cats.data.{EitherT, OptionT}
import domain.{ObjectStorage, StoredObject}

import java.io.InputStream



trait ObjectStorageService[F[_]] {

  def getStorages: F[Vector[ObjectStorage]]

  def listStorage(storageName: String): EitherT[F, Throwable, Vector[StoredObject]]

  def downloadObject(storageName: String, objectKey: String): EitherT[F, Throwable, InputStream]
}
