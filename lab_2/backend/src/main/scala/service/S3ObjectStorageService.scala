package service

import cats.data.EitherT
import cats.effect.std.Console
import cats.effect.{Async, Sync}
import cats.syntax.all._
import cats.{Applicative, Functor}
import domain.{ObjectStorage, StoredObject}
import fs2.{Chunk, Stream}
import service.spi.ObjectStorageService
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.model._
import software.amazon.awssdk.services.s3.{S3AsyncClient, S3Client}

import java.io.InputStream
import java.lang
import scala.jdk.CollectionConverters._

class S3ObjectStorageService[F[_] : Functor : Async : Console : Applicative](s3Client: S3Client, s3AsyncClient: S3AsyncClient) extends ObjectStorageService[F] {

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

  def deleteObject(storageName: String, objectKey: String): EitherT[F, Throwable, Int] =
    Sync[F].blocking(s3Client.deleteObject(
      DeleteObjectRequest.builder().bucket(storageName).key(objectKey).build()
    )).map(_.sdkHttpResponse().statusCode()).attemptT

  override def uploadObject(storageName: String, objectKey: String, `object`: Stream[F, Byte]): EitherT[F, Throwable, StoredObject] =
    (for {
      (uploadId, _) <- start(storageName, objectKey)
      completedParts <- upload(storageName, objectKey, uploadId, `object`.chunkMin(100000000))
        .onError(_ => abort(storageName, objectKey, uploadId) >> Applicative[F].unit)
      result <- complete(storageName, objectKey, uploadId, completedParts)
    } yield StoredObject(result.key())).attemptT

  private def start(bucket: String, destinationKey: String): F[(String, String)] = for {
    response <- Async[F].fromCompletableFuture(Async[F].blocking(s3AsyncClient.createMultipartUpload(
      CreateMultipartUploadRequest.builder
        .bucket(bucket)
        .key(destinationKey)
        .build
    )))
  } yield (response.uploadId(), response.abortRuleId())

  private def upload(storageName: String, objectKey: String, uploadId: String, parts: Stream[F, Chunk[Byte]]): F[Vector[CompletedPart]] = {
    parts.zipWithIndex.parEvalMapUnordered[F, CompletedPart](10) {
      case (part, index) =>
        for {
          _ <- Applicative[F].unit
          uploadPartRequest = UploadPartRequest.builder()
            .bucket(storageName)
            .key(objectKey)
            .uploadId(uploadId)
            .partNumber(index.toInt + 1)
            .build()
          requestBody = AsyncRequestBody.fromBytes(part.toArray)
          response <- Async[F].fromCompletableFuture(Async[F].blocking(s3AsyncClient.uploadPart(uploadPartRequest, requestBody)))
          completedPart = CompletedPart.builder()
            .partNumber(index.toInt + 1)
            .eTag(response.eTag())
            .build()
        } yield completedPart
    }.compile.toVector
  }

  private def complete(storageName: String, objectKey: String, uploadId: String, completedParts: Vector[CompletedPart]): F[CompleteMultipartUploadResponse] = {
    for {
      response <- Async[F].fromCompletableFuture(Async[F].blocking(s3AsyncClient.completeMultipartUpload(
        CompleteMultipartUploadRequest.builder
          .uploadId(uploadId)
          .bucket(storageName)
          .key(objectKey)
          .multipartUpload(
            CompletedMultipartUpload.builder
              .parts(completedParts.sortBy(_.partNumber()).asJava)
              .build()
          )
          .build()
      )))
    } yield response
  }

  private def abort(storageName: String, objectKey: String, uploadId: String): F[AbortMultipartUploadResponse] = for {
    response <- Async[F].fromCompletableFuture(Async[F].blocking(s3AsyncClient.abortMultipartUpload(AbortMultipartUploadRequest.builder()
      .uploadId(uploadId)
      .bucket(storageName)
      .key(objectKey)
      .build()
    )))
  } yield response

}

object S3ObjectStorageService {
  def apply[F[_] : Functor : Async : Console](s3Client: S3Client, s3AsyncClient: S3AsyncClient): S3ObjectStorageService[F] =
    new S3ObjectStorageService(s3Client, s3AsyncClient)
}