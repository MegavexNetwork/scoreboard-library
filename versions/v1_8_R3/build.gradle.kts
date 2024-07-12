plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

repositories {
  maven("https://repo.pgm.fyi/snapshots")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  compileOnly(libs.onePointEightPointEightNms)
  testImplementation(libs.onePointEightPointEightNms)
}
