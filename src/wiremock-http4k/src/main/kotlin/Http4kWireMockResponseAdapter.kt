package wiremock.http4k.wiremock.http4k

import org.http4k.core.MemoryBody
import org.http4k.core.MemoryResponse
import org.http4k.core.Status
import com.github.tomakehurst.wiremock.http.Response as WireMockResponse
import org.http4k.core.Response as Http4kResponse

fun WireMockResponse.toHttp4kResponse(): Http4kResponse = MemoryResponse(
  status = Status.fromCode(status)!!,
  headers = headers.all().flatMap { header ->
    header.values().map { value ->
      header.key to value
    }
  },
  body = MemoryBody(body),
  // protocol is private in wiremock.http.Response
  version = "HTTP/1.1",
)
