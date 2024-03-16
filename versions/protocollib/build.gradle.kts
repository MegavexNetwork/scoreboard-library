plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  mavenLocal() // temporary
  maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")
}
