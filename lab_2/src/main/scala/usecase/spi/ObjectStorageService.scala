package usecase.spi

import domain.{ObjectStorage, StoredObject}

trait ObjectStorageService[F[_]] {

  def getStorages: F[Vector[ObjectStorage]]

  def listStorage: F[Vector[StoredObject]]

  def downloadObject: F[String]
}
