# wiremock-http4k

An example, and working code, for transferring WireMock based tests from a traditional approach
passing HTTP traffic over a network port to an in memory approach where no network traffic is
needed.

See [WireMockWireMyFirstEmailClientSpec](https://github.com/wiremock/wiremock-http4k/blob/main/src/email/src/test/kotlin/WireMockWireMyFirstEmailClientSpec.kt)
for the traditional approach, with WireMock as an HTTP server listening on a TCP port and a real
HTTP client making the requests to that server, and
[WireMockInMemoryMyFirstEmailClientSpec](https://github.com/wiremock/wiremock-http4k/blob/main/src/email/src/test/kotlin/WireMockInMemoryMyFirstEmailClientSpec.kt)
for the corresponding in memory only implementation.
