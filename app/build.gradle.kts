/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("wiremock.http4k.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":utilities"))
    implementation(project(":list"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
}

application {
    // Define the main class for the application.
    mainClass.set("wiremock.http4k.app.AppKt")
}
