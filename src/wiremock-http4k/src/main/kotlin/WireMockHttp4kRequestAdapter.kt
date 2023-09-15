package wiremock.http4k.wiremock.http4k

import com.github.tomakehurst.wiremock.common.Urls.splitQuery
import com.github.tomakehurst.wiremock.http.CaseInsensitiveKey
import com.github.tomakehurst.wiremock.http.Cookie
import com.github.tomakehurst.wiremock.http.FormParameter
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import com.github.tomakehurst.wiremock.http.QueryParameter
import com.github.tomakehurst.wiremock.http.Request.Part
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.http.multipart.PartParser
import org.http4k.appendIfNotBlank
import org.http4k.core.Method
import org.http4k.core.cookie.cookies
import wiremock.http4k.email.http4k.isAbsolute
import wiremock.http4k.wiremock.WireMockRequestBase
import wiremock.org.eclipse.jetty.util.MultiMap
import wiremock.org.eclipse.jetty.util.UrlEncoded
import com.github.tomakehurst.wiremock.http.Request as WireMockRequest
import org.http4k.core.Request as Http4kRequest

class WireMockHttp4kRequestAdapter(
  private val http4KRequest: Http4kRequest,
) : WireMockRequestBase {

  private val _httpHeaders =
    HttpHeaders(
      http4KRequest.headers
        .map { (name, value) -> CaseInsensitiveKey(name) to value!! }
        .groupBy({ it.first }, { it.second })
        .map { (name, values) -> HttpHeader(name, values) },
    )

  private val url = StringBuilder()
    .append(
      when {
        http4KRequest.uri.path.isBlank() || http4KRequest.uri.path.startsWith("/") ->
          http4KRequest.uri.path

        else -> "/${http4KRequest.uri.path}"
      },
    )
    .appendIfNotBlank(http4KRequest.uri.query, "?", http4KRequest.uri.query)
    .toString()

  override fun getUrl(): String = url

  private val absoluteUrl = if (http4KRequest.uri.isAbsolute()) {
    http4KRequest.uri.toString()
  } else {
    null
  }

  override fun getAbsoluteUrl(): String? = absoluteUrl

  private val method = when (http4KRequest.method) {
    Method.GET -> RequestMethod.GET
    Method.POST -> RequestMethod.POST
    Method.PUT -> RequestMethod.PUT
    Method.DELETE -> RequestMethod.DELETE
    Method.OPTIONS -> RequestMethod.OPTIONS
    Method.TRACE -> RequestMethod.TRACE
    Method.PATCH -> RequestMethod.PATCH
    Method.PURGE -> throw UnsupportedOperationException(
      "HTTP method PURGE is not supported in WireMock"
    )

    Method.HEAD -> RequestMethod.HEAD
  }

  override fun getMethod(): RequestMethod = method

  override fun getScheme(): String? {
    return http4KRequest.uri.scheme.ifEmpty { null }
  }

  private val host = run {
    val hostHeader = header("Host")
    if (hostHeader.isPresent) {
      hostHeader.firstValue().substringBefore(":")
    } else {
      http4KRequest.source?.address
    }
  }

  override fun getHost(): String? = host

  private val _port by lazy {
    val hostHeader = header("Host")
    if (hostHeader.isPresent && hostHeader.firstValue().contains(":")) {
      hostHeader.firstValue().substringAfter(':').toInt()
    } else {
      http4KRequest.source?.port ?: 80
    }
  }

  override fun getPort(): Int = _port

  private val header = getHeader("X-Forwarded-For")

  override fun getClientIp(): String? = header

  override fun getHeaders(): HttpHeaders = _httpHeaders

  private val queryParams by lazy { splitQuery(http4KRequest.uri.query) }

  override fun queryParameter(key: String): QueryParameter {
    return queryParams[key] ?: QueryParameter.absent(key)
  }

  private val formParameters by lazy {
    val contentTypeHeader = contentTypeHeader()
    if (contentTypeHeader.mimeTypePart()?.contains("application/x-www-form-urlencoded") != true) {
      emptyMap<String, FormParameter>()
    }
    val formParameterMultimap: MultiMap<String> = MultiMap()
    val charset = contentTypeHeader.charset()
    UrlEncoded.decodeTo(bodyAsString, formParameterMultimap, charset)
    formParameterMultimap
      .mapValues { (name, values) -> FormParameter(name, values) }
  }

  override fun formParameters(): Map<String, FormParameter> = formParameters

  private val _cookies by lazy {
    http4KRequest.cookies()
      .groupBy { it.name }
      .map { (name, values) -> Cookie(name, values.map { it.value }) }
      .associateBy { it.key }
  }

  override fun getCookies(): Map<String, Cookie> = _cookies

  override fun getBody(): ByteArray = http4KRequest.body.payload.array()

  override fun isMultipart(): Boolean {
    return getHeader("Content-Type")?.contains("multipart/") ?: false
  }

  @Volatile
  private var cachedMultiparts: Collection<Part>? = null

  override fun getParts(): Collection<Part>? {
    if (!isMultipart) {
      return null
    }

    if (cachedMultiparts == null) {
      cachedMultiparts = PartParser.parseFrom(this)
    }

    return if (cachedMultiparts?.isEmpty() == false) cachedMultiparts else null
  }

  override fun isBrowserProxyRequest(): Boolean = false

  override fun getProtocol(): String = http4KRequest.version
}

fun Http4kRequest.toWireMockRequest(): WireMockRequest {
  return WireMockHttp4kRequestAdapter(this)
}
