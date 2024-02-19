plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
}
