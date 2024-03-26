plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly("com.github.retrooper.packetevents:spigot:2.2.1")
}
