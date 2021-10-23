package domain.spi

import domain.model.{TranslateFile, TranslateText}

trait TranslateService[F[_]] {
  def translate(input: TranslateText.Input): F[Either[Throwable, TranslateText.Output]]
  def translateFile(input: TranslateFile.Input[F]): F[Either[Throwable, TranslateFile.Output[F]]]
}

