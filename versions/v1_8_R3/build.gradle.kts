plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  // Alias to legacy for backwards compatibility
  implementation(project(":scoreboard-library-legacy"))
}
