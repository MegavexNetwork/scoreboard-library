plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

dependencies {
  api(project(":api"))
  implementation(project(":packet-adapter-base"))
}
