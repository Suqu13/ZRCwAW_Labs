package service.spi

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import domain.{ObjectStorage, StoredObject}
import fs2.{Chunk, Stream}

import java.io.InputStream



trait ObjectStorageService[F[_]] {

  type ObjectLocation = String

  def getStorages: F[Vector[ObjectStorage]]

  def listStorage(storageName: String): EitherT[F, Throwable, Vector[StoredObject]]

  def downloadObject(storageName: String, objectKey: String): EitherT[F, Throwable, InputStream]

  def deleteObject(storageName: String, objectKey: String): EitherT[F, Throwable, Int]

  def uploadObject(storageName: String, objectKey: String, `object`: Stream[F, Byte]): EitherT[F, Throwable, StoredObject]
}
