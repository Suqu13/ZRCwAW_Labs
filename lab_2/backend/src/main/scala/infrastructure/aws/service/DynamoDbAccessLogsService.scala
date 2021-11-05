package infrastructure.aws.service

import cats.effect.kernel.Sync
import cats.effect.std.Console
import cats.instances.list._
import cats.syntax.all._
import domain.model.{AccessLog, User}
import domain.spi._
import org.http4s.{Method, Request, Response}
import org.scanamo.generic.auto._
import org.scanamo.syntax._
import org.scanamo.{Scanamo, Table}
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

import java.time.Instant

class DynamoDbAccessLogsService[F[_] : Sync : Console](dynamoDbClient: DynamoDbClient) extends AccessLogsService[F] {

  private lazy val dbExecutor = Scanamo(dynamoDbClient)
  private lazy val accessLogsTable = Table[AccessLog]("AccessLogs")

  override def log(request: Request[F], response: Response[F], user: Option[User]): F[Either[Throwable, AccessLog]] = for {
    _ <- Sync[F].unit
    accessLog = createAccessLog(request, response, user.map(_.login))
    result <- Sync[F].blocking(dbExecutor.exec(accessLogsTable.put(accessLog))).attempt
  } yield result.map(_ => accessLog)


  private def createAccessLog(request: Request[F], response: Response[F], userLogin: Option[String]): AccessLog = {
    //WARN: Fields with `getOrElse` should have `Option` type within `AccessLogs` but
    // Scanamo is impaired and do not know how to deal with complicated filters - its Type System is fuc...
    val server = request.server.map(_.toString()).getOrElse("")
    val httpVersion = request.httpVersion.toString()
    val authority = request.uri.authority.map(_.toString()).getOrElse("")
    val uri = request.uri.toString()
    val method = request.method.name
    val responseStatus = response.status.code
    val requestContentType = request.contentType.map(_.mediaType.toString()).getOrElse("")
    val requestBodySize = request.contentLength.getOrElse(0L)
    val responseContentType = response.contentType.map(_.mediaType.toString()).getOrElse("")
    val responseBodySize = response.contentLength.getOrElse(0L)
    AccessLog(server, httpVersion, authority, uri, method, requestContentType, requestBodySize, responseStatus, responseContentType, responseBodySize, userLogin.getOrElse(""))
  }

  override def fetchAccessLogs(
                                userLogin: Option[String],
                                method: Option[Method],
                                from: Option[Instant],
                                to: Option[Instant]
                              ): F[Either[Throwable, List[AccessLog]]] = {
    val userCondition = "userLogin" beginsWith userLogin.getOrElse("")
    val methodCondition = "method" beginsWith method.fold("")(x => x.name)
    val timestampCondition = "timestamp" between
      from.map(x => x.toEpochMilli).getOrElse(Long.MinValue) and to.map(x => x.toEpochMilli).getOrElse(Long.MaxValue)

    for {
      queryResult <- Sync[F].blocking(
        dbExecutor.exec(
          accessLogsTable
            .filter(timestampCondition and methodCondition and userCondition)
            .scan()
        )
      )
    } yield queryResult.traverse(_.left.map(List(_)).toValidated).toEither
      .leftMap(errors =>
        AccessLogsFetchError(s"Cannot fetch [${errors.size}] access logs")
      )
  }

}
