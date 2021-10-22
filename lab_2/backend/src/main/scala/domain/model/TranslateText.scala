package domain.model

object TranslateText {
  case class Input(text: String, sourceLanguage: String, targetLanguage: String)
  case class Output(text: String)
}
