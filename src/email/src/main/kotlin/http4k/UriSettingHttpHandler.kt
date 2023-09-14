package wiremock.http4k.email.http4k

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.extend

class UriSettingHttpHandler(
  private val baseUri: () -> Uri,
  private val delegate: HttpHandler,
) : HttpHandler {
  override fun invoke(request: Request): Response {
    val requestWithFullUri = if (request.uri.isAbsolute()) {
      request
    } else {
      request.uri(baseUri().extend(request.uri))
    }
    return delegate.invoke(requestWithFullUri)
  }
}
