plugins {
  `maven-publish`
  id("net.megavex.scoreboardlibrary.base-conventions")
  id("io.papermc.paperweight.userdev") version "1.5.12"
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
}

tasks {
  assemble {
    dependsOn(reobfJar)
  }
}

publishing {
  publications.create<MavenPublication>("maven") {
    artifact(tasks.reobfJar)
  }
}

java {
  disableAutoTargetJvm()
}
