package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory
import org.http4k.core.HttpHandler
import wiremock.http4k.wiremock.http4k.toHttp4kResponse
import wiremock.http4k.wiremock.http4k.toWireMockRequest
import org.http4k.core.Request as Http4KRequest

private val serverFactory = DirectCallHttpServerFactory()

class WireMockInMemorySendgridEmailClientSpec : SendgridEmailClientSpec(
  client = serverFactory.toHttp4kHandler(),
  sendgridMock = WireMockServer(options().httpServerFactory(serverFactory)),
)

private fun DirectCallHttpServerFactory.toHttp4kHandler(): HttpHandler =
  { http4kRequest: Http4KRequest ->
    val wireMockRequest = http4kRequest.toWireMockRequest()
    val wireMockResponse = httpServer.stubRequest(wireMockRequest)
    wireMockResponse.toHttp4kResponse()
  }
