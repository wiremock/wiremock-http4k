package wiremock.http4k.email

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import org.http4k.client.ApacheClient

class WireMockWireSendgridEmailClientSpec : SendgridEmailClientSpec(
  ApacheClient(),
  WireMockServer(options().dynamicPort()),
)
