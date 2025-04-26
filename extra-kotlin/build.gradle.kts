import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  alias(libs.plugins.kotlin)
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":scoreboard-library-api"))
  testImplementation(kotlin("test"))
  compileOnly(libs.spigotApi)
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
