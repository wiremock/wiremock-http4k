package wiremock.http4k.email.wiremock.http4k

import org.http4k.core.Body
import org.http4k.core.Headers
import org.http4k.core.MemoryBody
import org.http4k.core.Response
import org.http4k.core.Status
import java.io.InputStream
import com.github.tomakehurst.wiremock.http.Response as WireMockResponse
import org.http4k.core.Response as Http4kResponse

class Http4kWireMockResponseAdapter(
  wireMockResponse: WireMockResponse,
) : Http4kResponse {
  override val body: Body = MemoryBody(wireMockResponse.body)
  override val headers: Headers = wireMockResponse.headers.all().flatMap { header ->
    header.values().map { value ->
      header.key to value
    }
  }
  override val status: Status = Status.fromCode(wireMockResponse.status)!!
  override val version: String = "HTTP/1.1" // protocol is private in wiremock.http.Response

  override fun body(body: InputStream, length: Long?): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun body(body: String): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun body(body: Body): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun header(name: String, value: String?): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun headers(headers: Headers): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun removeHeader(name: String): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun removeHeaders(prefix: String): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun replaceHeader(name: String, value: String?): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun replaceHeaders(source: Headers): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }

  override fun status(new: Status): Response {
    throw UnsupportedOperationException("copy operations are not supported in this adapter")
  }
}

fun WireMockResponse.toHttp4kResponse(): Http4kResponse = Http4kWireMockResponseAdapter(this)
