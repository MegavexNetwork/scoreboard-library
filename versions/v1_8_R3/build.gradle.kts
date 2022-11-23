plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  maven("https://nexus.funkemunky.cc/content/repositories/releases/")
}

dependencies {
  compileOnly(project(":packet-adapter-base"))
  compileOnly("org.github.spigot:1.8.8:1.8.8")
}
