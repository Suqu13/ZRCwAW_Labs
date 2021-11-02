package infrastructure.security

import org.reactormonk.{CryptoBits, PrivateKey}

import java.time.Clock

class Encryptor(pass: String) {
  private val key = PrivateKey(scala.io.Codec.toUTF8(pass))
  private val crypto = CryptoBits(key)
  private val clock = Clock.systemUTC()

  def encryptToken(data: String): String = crypto.signToken(data, clock.millis().toString)

  def decryptToken(token: String): Option[String] = crypto.validateSignedToken(token)

}

