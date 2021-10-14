package service

import cats.Functor
import cats.data.EitherT
import cats.effect.std.Console
import cats.effect.{Async, Sync}
import cats.syntax.all._
import domain.{ObjectStorage, StoredObject}
import service.spi.ObjectStorageService
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.{GetObjectRequest, ListObjectsV2Request}

import java.io.InputStream
import scala.jdk.CollectionConverters._

class S3ObjectStorageService[F[_]: Functor : Sync : Console](s3Client: S3Client) extends ObjectStorageService[F] {


  override def getStorages: F[Vector[ObjectStorage]] =
    Sync[F].blocking(s3Client.listBuckets)
      .map(_.buckets.asScala.toVector.map(b => ObjectStorage(b.name)))

  override def listStorage(storageName: String): EitherT[F, Throwable, Vector[StoredObject]] =
    Sync[F].blocking(s3Client.listObjectsV2(
      ListObjectsV2Request.builder().bucket(storageName).build()
    )).map(_.contents.asScala.toVector.map(s => StoredObject(s.key))).attemptT

  override def downloadObject(storageName: String, objectKey: String): EitherT[F, Throwable, InputStream] =
    Sync[F].blocking(s3Client.getObjectAsBytes(
      GetObjectRequest.builder().bucket(storageName).key(objectKey).build(),
    )).map(_.asInputStream()).attemptT
}

object S3ObjectStorageService {
  def apply[F[_] : Functor : Async : Console](s3Client: S3Client): S3ObjectStorageService[F] =
    new S3ObjectStorageService(s3Client)
}