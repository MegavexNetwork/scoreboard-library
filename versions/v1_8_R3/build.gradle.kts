plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  // Alias to legacy for backwards compat
  implementation(project(":scoreboard-library-legacy"))
}
