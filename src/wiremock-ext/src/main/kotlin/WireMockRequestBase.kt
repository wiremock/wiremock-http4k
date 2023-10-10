package wiremock.http4k.wiremock

import com.github.tomakehurst.wiremock.http.ContentTypeHeader
import com.github.tomakehurst.wiremock.http.FormParameter
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.Request
import java.util.Optional
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface WireMockRequestBase : Request {

  override fun header(key: String): HttpHeader {
    return headers.getHeader(key)
  }

  override fun getHeader(key: String): String? {
    val header = header(key)
    return if (header.isPresent) header.firstValue() else null
  }

  override fun contentTypeHeader(): ContentTypeHeader {
    return headers.contentTypeHeader
  }

  override fun containsHeader(key: String): Boolean {
    return header(key).isPresent
  }

  override fun getAllHeaderKeys(): MutableSet<String> {
    return headers.keys()
  }

  override fun formParameter(key: String): FormParameter? {
    return formParameters()?.get(key)
  }

  override fun getBodyAsString(): String {
    return String(body)
  }

  @OptIn(ExperimentalEncodingApi::class)
  override fun getBodyAsBase64(): String {
    return Base64.encode(body)
  }

  override fun getPart(name: String): Request.Part? {
    return parts?.find { it.name == name }
  }

  override fun getOriginalRequest(): Optional<Request> {
    return Optional.empty()
  }
}
