plugins {
  id("io.papermc.paperweight.userdev") version "1.3.5"
  `maven-publish`
}

dependencies {
  compileOnly(project(":nms-base"))
  paperDevBundle("1.19.2-R0.1-SNAPSHOT")
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
