package infrastructure.aws.service

import cats.data.EitherT
import cats.effect.Sync
import cats.effect.std.Console
import cats.syntax.all._
import cats.{Applicative, Functor}
import domain.spi.{LanguageAnalysisError, LanguageAnalysisService}
import domain.model.{DetectedLanguage, LanguageAnalysisResult, SentimentAnalysisResult, SentimentScore}
import software.amazon.awssdk.services.comprehend.ComprehendClient
import software.amazon.awssdk.services.comprehend.model.{DetectDominantLanguageRequest, DetectSentimentRequest}

import scala.jdk.CollectionConverters._


class ComprehendLanguageAnalysisService[F[_] : Functor : Sync : Console : Applicative](
  comprehendClient: ComprehendClient
) extends LanguageAnalysisService[F] {

  override def sentimentAnalysis(text: String): EitherT[F, LanguageAnalysisError, SentimentAnalysisResult] =
    EitherT(for {
      lanRes <- languageAnalysis(text)
      sentiment <- (if (lanRes.detectedLanguages.isEmpty) {
        Applicative[F].pure(Either.left(LanguageAnalysisError("Cannot detect language")))
      } else {
        val dominantLang = lanRes.detectedLanguages.sortBy(_.score).reverse
        Sync[F].blocking(comprehendClient.detectSentiment(
          DetectSentimentRequest.builder().languageCode(dominantLang.head.code).text(text).build()
        )).map(r => SentimentAnalysisResult(r.sentimentAsString(), SentimentScore(
          neutral = r.sentimentScore().neutral(),
          positive = r.sentimentScore().positive(),
          negative = r.sentimentScore().negative(),
          mixed = r.sentimentScore().mixed()
        ))).map(s => Either.right(s))
      })
    } yield sentiment)


  override def languageAnalysis(text: String): F[LanguageAnalysisResult] =
    Sync[F].blocking(comprehendClient.detectDominantLanguage(
      DetectDominantLanguageRequest.builder().text(text).build()
    )).map(r =>
      LanguageAnalysisResult(r.languages().asScala.toVector.map(
        l => DetectedLanguage(l.languageCode(), l.score()))
      )
    )
}
