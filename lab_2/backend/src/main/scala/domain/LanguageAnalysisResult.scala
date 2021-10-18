package domain

case class LanguageAnalysisResult(
  detectedLanguages: Vector[DetectedLanguage]
)

case class DetectedLanguage(
  code: String,
  score: Float
)
