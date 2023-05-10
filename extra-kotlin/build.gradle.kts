plugins {
  kotlin("jvm") version "1.8.21"
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":scoreboard-library-api"))
  testImplementation(kotlin("test"))
}
