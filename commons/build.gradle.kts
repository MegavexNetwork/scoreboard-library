plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  api(project(":scoreboard-library-api"))
  compileOnly(libs.spigotApi)
}
