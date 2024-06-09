plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

repositories {
  maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly(libs.packetEvents)
}
