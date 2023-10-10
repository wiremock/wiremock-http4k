plugins {
  id("wiremock.http4k.kotlin-library-conventions")
}

dependencies {

  implementation(platform("org.http4k:http4k-bom:5.8.4.0"))

  api("org.http4k:http4k-core")
  api("org.wiremock:wiremock-standalone:3.2.0")
  api(project(":wiremock-ext"))
  implementation(project(":http4k-ext"))

}
