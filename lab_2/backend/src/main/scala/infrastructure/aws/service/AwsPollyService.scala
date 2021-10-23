package infrastructure.aws.service

import cats.effect.Async
import cats.syntax.all._
import domain.model.ReadText
import domain.spi._
import fs2.io.readInputStream
import software.amazon.awssdk.core.ResponseBytes
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.polly.PollyAsyncClient
import software.amazon.awssdk.services.polly.model.{DescribeVoicesRequest, OutputFormat, SynthesizeSpeechRequest, SynthesizeSpeechResponse}


class AwsPollyService[F[_] : Async](pollyAsyncClient: PollyAsyncClient) extends ReadService[F] {
  override def read(input: ReadText.Input): F[Either[Throwable, ReadText.Output[F]]] = {
    val output = for {
      _ <- Async[F].unit
      describeVoiceRequest = DescribeVoicesRequest.builder()
        .languageCode(input.language)
        .build()
      describeVoiceResponse <- Async[F].fromCompletableFuture(
        Async[F].blocking(pollyAsyncClient.describeVoices(describeVoiceRequest))
      )
      synthesizeSpeechRequest = SynthesizeSpeechRequest
        .builder()
        .text(input.text)
        .voiceId(describeVoiceResponse.voices().get(0).id())
        .engine(describeVoiceResponse.voices().get(0).supportedEngines().get(0))
        .outputFormat(OutputFormat.MP3)
        .build()
      synthesizeSpeechResponse <- Async[F].fromCompletableFuture(Async[F].blocking(
        pollyAsyncClient.synthesizeSpeech[ResponseBytes[SynthesizeSpeechResponse]](synthesizeSpeechRequest, AsyncResponseTransformer.toBytes))
      )
    } yield ReadText.Output(readInputStream(Async[F].pure(synthesizeSpeechResponse.asInputStream()), 1024))

    output.attempt
  }
}
