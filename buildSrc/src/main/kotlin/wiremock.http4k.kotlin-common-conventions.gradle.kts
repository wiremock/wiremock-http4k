plugins {
  id("org.jetbrains.kotlin.jvm")
  id("org.jmailen.kotlinter")
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

dependencyLocking {
  lockAllConfigurations()
}
