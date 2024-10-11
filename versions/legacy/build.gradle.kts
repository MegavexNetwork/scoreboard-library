plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
}
