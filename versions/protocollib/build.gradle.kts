plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

repositories {
  maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly(libs.protocollib)
}
