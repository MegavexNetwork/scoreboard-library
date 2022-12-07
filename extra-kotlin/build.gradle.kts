plugins {
  kotlin("jvm") version "1.7.21"
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

kotlin {
  explicitApi()
}

dependencies {
  api(project(":api"))
}
