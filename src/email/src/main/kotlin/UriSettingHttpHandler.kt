package wiremock.http4k.email

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Uri
import org.http4k.core.extend

class UriSettingHttpHandler(
  private val baseUri: Uri,
  private val delegate: HttpHandler,
) : HttpHandler {
  override fun invoke(request: Request): Response {
    val fullUri = if (request.uri.scheme.isEmpty()) baseUri.extend(request.uri) else request.uri
    return delegate.invoke(request.uri(fullUri))
  }
}
