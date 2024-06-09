plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  api(project(":scoreboard-library-api"))
  implementation(project(":scoreboard-library-packet-adapter-base"))
}
