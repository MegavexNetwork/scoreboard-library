plugins {
  id("io.papermc.paperweight.userdev") version "1.3.5"
  `maven-publish`
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  paperDevBundle("1.19.3-R0.1-SNAPSHOT")
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
