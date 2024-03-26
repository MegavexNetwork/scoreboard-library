import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  id("net.megavex.scoreboardlibrary.publish-conventions")
  kotlin("jvm") version "1.9.23"
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":scoreboard-library-api"))
  testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
