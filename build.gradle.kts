import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.gundy.semver4j.model.Version

plugins {
  base
  kotlin("jvm") apply false
  id("org.jmailen.kotlinter")
  id("com.autonomousapps.dependency-analysis") version "1.21.0"
  id("com.dorongold.task-tree") version "2.1.1"
  id("com.github.ben-manes.versions") version "0.48.0"
}

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath(group = "com.github.gundy", name = "semver4j", version = "0.16.4")
  }
}

tasks {
  check {
    dependsOn("buildHealth")
    dependsOn("installKotlinterPrePushHook")
  }
}

dependencyAnalysis {
  issues {
    // configure for all projects
    all {
      // set behavior for all issue types
      onAny {
        severity("fail")
      }
    }
  }
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    candidate.version.isPreRelease()
  }
}

fun String.isPreRelease(): Boolean = try {
  Version.fromString(this).preReleaseIdentifiers.isNotEmpty()
} catch (e: IllegalArgumentException) {
  false
}
