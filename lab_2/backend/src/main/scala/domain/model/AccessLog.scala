package domain.model

import java.time.Instant
import java.util.UUID

case class AccessLog(
                      server: String,
                      httpVersion: String,
                      authority: String,
                      uri: String,
                      method: String,
                      requestContentType: String,
                      requestBodySize: Long,
                      responseStatus: Int,
                      responseContentType: String,
                      responseBodySize: Long,
                      userLogin: String,
                      id: String = UUID.randomUUID().toString,
                      timestamp: Instant = Instant.now()
                    )


