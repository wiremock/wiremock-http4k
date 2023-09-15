plugins {
  // Apply the common convention plugin for shared build configuration between library and application projects.
  id("wiremock.http4k.kotlin-common-conventions")

  // Apply the java-library plugin for API and implementation separation.
  `java-library`
}

dependencies {

  val kotestVersion = "5.7.2"
  testImplementation(platform("io.kotest:kotest-bom:$kotestVersion"))
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
  // Use JUnit Platform for unit tests.
  useJUnitPlatform()
  systemProperty("kotest.framework.classpath.scanning.config.disable", "true")
  systemProperty("kotest.framework.classpath.scanning.autoscan.disable", "true")
}
