package wiremock.http4k.wiremock.http4k

import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory
import org.http4k.core.HttpHandler
import org.http4k.core.Request as Http4KRequest

fun DirectCallHttpServerFactory.toHttp4kHandler(): HttpHandler = { http4kRequest: Http4KRequest ->
  val wireMockRequest = http4kRequest.toWireMockRequest()
  val wireMockResponse = httpServer.stubRequest(wireMockRequest)
  wireMockResponse.toHttp4kResponse()
}
