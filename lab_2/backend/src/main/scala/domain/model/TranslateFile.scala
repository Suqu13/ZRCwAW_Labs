package domain.model

import fs2.Stream

object TranslateFile {
  case class Input[F[_]](fileContent: Stream[F, Byte], sourceLanguage: String, targetLanguage: String)
  case class Output[F[_]](fileContent: Stream[F, Byte])
}
