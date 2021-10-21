package domain.model

case class LanguageAnalysisResult(
  detectedLanguages: Vector[DetectedLanguage]
)

case class DetectedLanguage(
  code: String,
  score: Float
)
