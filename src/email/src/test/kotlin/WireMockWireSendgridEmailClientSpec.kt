package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.http4k.client.ApacheClient
import org.http4k.core.Uri
import wiremock.http4k.email.http4k.UriSettingHttpHandler

val sendgridMock = WireMockServer(options().dynamicPort())

class WireMockWireSendgridEmailClientSpec : SendgridEmailClientSpec(
  UriSettingHttpHandler(
    baseUri = { sendgridMock.baseUrl().toUri() },
    ApacheClient()
  ),
  sendgridMock,
)

private fun String.toUri() = Uri.of(this)
