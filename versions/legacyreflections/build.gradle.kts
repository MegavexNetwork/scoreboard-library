plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

repositories {
  maven("https://repo.pgm.fyi/snapshots")
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))

  // Even if we don't need it to compile, we still need it for the tests to run
  testImplementation(libs.onePointEightPointEightNms)
}
