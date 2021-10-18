package domain

case class SentimentAnalysisResult(
  `type`: String,
  scores: SentimentScore
)

case class SentimentScore(
  neutral: Float,
  positive: Float,
  negative: Float,
  mixed: Float,
)
