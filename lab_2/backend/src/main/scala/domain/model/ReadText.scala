package domain.model

import fs2.Stream

object ReadText {
  case class Input(text: String, language: String)
  case class Output[F[_]](voice: Stream[F, Byte])
}
