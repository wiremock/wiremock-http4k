package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.http4k.client.ApacheClient
import wiremock.http4k.email.http4k.UriSettingHttpHandler
import wiremock.http4k.email.http4k.toUri

private val sendgridMock = WireMockServer(options().dynamicPort())

class WireMockWireSendgridEmailClientSpec : SendgridEmailClientSpec(
  UriSettingHttpHandler(
    baseUri = { sendgridMock.baseUrl().toUri() },
    ApacheClient()
  ),
  sendgridMock,
)
