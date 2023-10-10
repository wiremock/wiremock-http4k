plugins {
  id("wiremock.http4k.kotlin-library-conventions")
}

dependencies {
  api("org.http4k:http4k-core")

  implementation(platform("org.http4k:http4k-bom:5.8.4.0"))
}
