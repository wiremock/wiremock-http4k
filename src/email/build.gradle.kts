plugins {
  id("wiremock.http4k.kotlin-library-conventions")
}

dependencies {
  api("org.http4k:http4k-core")

  implementation(platform("org.http4k:http4k-bom:5.8.0.0"))
  implementation("org.http4k:http4k-format-jackson")

  testImplementation("org.http4k:http4k-client-apache")
  testImplementation("io.kotest.extensions:kotest-extensions-wiremock:2.0.1")
  testImplementation("org.wiremock:wiremock-standalone:3.0.4")
  testImplementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")

  modules {
    module("com.github.tomakehurst:wiremock-jre8-standalone") {
      replacedBy("org.wiremock:wiremock-standalone")
    }
  }
}
