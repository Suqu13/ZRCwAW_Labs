package service

import cats.Functor
import cats.data.EitherT
import cats.effect.std.Console
import cats.effect.Sync
import com.amazonaws.services.s3.AmazonS3
import domain.{ObjectStorage, StoredObject}
import service.spi.ObjectStorageService
import cats.syntax.all._

import scala.jdk.CollectionConverters._
import java.io.InputStream

class S3ObjectStorageService[F[_]: Functor : Sync : Console](s3Client: AmazonS3) extends ObjectStorageService[F] {


  override def getStorages: F[Vector[ObjectStorage]] =
    Sync[F].blocking(s3Client.listBuckets().asScala.toVector.map(s => ObjectStorage(s.getName)))

  override def listStorage(storageName: String): EitherT[F, Throwable, Vector[StoredObject]] =
    Sync[F].blocking(s3Client.listObjects(storageName).getObjectSummaries.asScala.toVector.map(s => StoredObject(s.getKey))).attemptT

  override def downloadObject(storageName: String, objectKey: String): EitherT[F, Throwable, InputStream] =
    Sync[F].blocking(s3Client.getObject(storageName, objectKey)).map[InputStream](_.getObjectContent)
      .attemptT
}
