plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
}

dependencies {
  compileOnlyApi(libs.bundles.adventure)
  compileOnly(libs.spigotApi)
}
