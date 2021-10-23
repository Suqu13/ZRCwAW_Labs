package infrastructure.aws.service

import cats.effect.Async
import cats.syntax.all._
import domain.model.{TranslateFile, TranslateText}
import domain.spi._
import fs2.Stream
import software.amazon.awssdk.services.translate.TranslateAsyncClient
import software.amazon.awssdk.services.translate.model.TranslateTextRequest

import java.nio.charset.StandardCharsets

class AwsTranslateService[F[_] : Async](translateAsyncClient: TranslateAsyncClient) extends TranslateService[F] {
  override def translate(input: TranslateText.Input): F[Either[Throwable, TranslateText.Output]] = {
    val output = for {
      _ <- Async[F].unit
      translateTextRequest = TranslateTextRequest
        .builder()
        .text(input.text)
        .sourceLanguageCode(input.sourceLanguage)
        .targetLanguageCode(input.targetLanguage)
        .build()
      translateTextResponse <- Async[F].fromCompletableFuture(Async[F].blocking(translateAsyncClient.translateText(translateTextRequest)))
    } yield TranslateText.Output(translateTextResponse.translatedText())

    output.attempt
  }

  override def translateFile(input: TranslateFile.Input[F]): F[Either[Throwable, TranslateFile.Output[F]]] = for {
    bytes <- input.fileContent.compile.toVector
    text = new String(bytes.toArray, StandardCharsets.UTF_8)
    outputText <- translate(TranslateText.Input(text, input.sourceLanguage, input.targetLanguage))
      .map(_.map(_.text))
    output = outputText.map(t =>
      TranslateFile.Output(
      Stream.emits(t.getBytes(StandardCharsets.UTF_8)).covary[F])
    )
  } yield output

}
