import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.23"
  id("net.megavex.scoreboardlibrary.publish-conventions")
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
