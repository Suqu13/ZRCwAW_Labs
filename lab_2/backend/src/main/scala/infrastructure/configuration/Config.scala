package infrastructure.configuration

import cats.effect.Sync
import domain.model.User
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

case class HttpServerConfig(
  host: String,
  port: Int
)

case class AwsSdkConfig(
  secretKeyId: String,
  secretAccessKey: String,
  sessionToken: String
)

case class ApiConfig(
  adminUser: User
)

case class EncryptionConfig(
  key: String
)

case class Config(
  httpServer: HttpServerConfig,
  awsSdk: AwsSdkConfig,
  api: ApiConfig,
  encryption: EncryptionConfig,
)

object Config {
  def load[F[_] : Sync]: F[Config] =
    ConfigSource.default.loadF[F, Config]()
}
