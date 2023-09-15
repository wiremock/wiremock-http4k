package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory
import wiremock.http4k.wiremock.http4k.toHttp4kHandler

private val serverFactory = DirectCallHttpServerFactory()

class WireMockInMemorySendgridEmailClientSpec : SendgridEmailClientSpec(
  client = serverFactory.toHttp4kHandler(),
  sendgridMock = WireMockServer(options().httpServerFactory(serverFactory)),
)
