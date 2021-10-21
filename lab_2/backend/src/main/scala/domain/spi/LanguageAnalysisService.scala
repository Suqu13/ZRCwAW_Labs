package domain.spi

import cats.data.EitherT
import domain.model.SentimentAnalysisResult
import domain.model.{LanguageAnalysisResult, SentimentAnalysisResult}

case class LanguageAnalysisError(msg: String) extends Throwable(msg)

trait LanguageAnalysisService[F[_]] {

  def sentimentAnalysis(text: String): EitherT[F, LanguageAnalysisError, SentimentAnalysisResult]

  def languageAnalysis(text: String): F[LanguageAnalysisResult]

}
