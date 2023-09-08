plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}



configure<JavaPluginExtension> {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

    @Suppress("UnstableApiUsage")
    consistentResolution {
        useCompileClasspathVersions()
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencyLocking {
    lockAllConfigurations()
}
