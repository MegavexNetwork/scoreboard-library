plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

repositories { maven("https://repo.pgm.fyi/snapshots") } //temp

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT") //temp
}
