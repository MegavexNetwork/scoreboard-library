plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  maven("https://repo.papermc.io/repository/maven-public/")
  maven("https://repo.codemc.io/repository/maven-snapshots/")
}

dependencies {
  compileOnly(project(":nms-base"))
  compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
  compileOnly("com.github.retrooper.packetevents:spigot:2.0.0-SNAPSHOT")
}
