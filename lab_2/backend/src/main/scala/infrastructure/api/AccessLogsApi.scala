package infrastructure.api

import cats.data.EitherT
import cats.effect.Async
import domain.model.User
import domain.spi.AccessLogsService
import infrastructure.http.AuthedHttpApi
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl._
import org.http4s.{AuthedRoutes, Method, QueryParamDecoder}

import java.time.Instant


class AccessLogsApi[F[_] : Async](accessLogsService: AccessLogsService[F]) extends AuthedHttpApi[F, User] {

  val routes: AuthedRoutes[User, F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    implicit val methodQueryParamDecoder: QueryParamDecoder[Method] =
      QueryParamDecoder[String].map(x => Method.fromString(x).right.get)

    implicit val instantQueryParamDecoder: QueryParamDecoder[Instant] =
      QueryParamDecoder[String].map(x => Instant.parse(x))

    object UserLoginQueryParamMatcher extends OptionalQueryParamDecoderMatcher[String]("userLogin")
    object MethodQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Method]("method")
    object FromQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Instant]("from")
    object ToQueryParamMatcher extends OptionalQueryParamDecoderMatcher[Instant]("to")


    AuthedRoutes.of[User, F] {
      case GET -> Root / "accessLogs" :? UserLoginQueryParamMatcher(userLogin) +& MethodQueryParamMatcher(method) +& FromQueryParamMatcher(from) +& ToQueryParamMatcher(to) as _ =>
        EitherT(accessLogsService.fetchAccessLogs(userLogin, method, from, to)).foldF(
          e => BadRequest(e.getMessage),
          s => Ok(s)
        )
    }
  }
}
