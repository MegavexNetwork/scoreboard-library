plugins {
  id("io.papermc.paperweight.userdev") version "1.3.5"
  `maven-publish`
}

repositories {
  maven { url = uri("https://jitpack.io") }
}

dependencies {
  compileOnly(project(":nms-base"))

  paperDevBundle("1.19.2-R0.1-SNAPSHOT")
  compileOnly("com.github.ProtocolSupport:ProtocolSupport:05b7689664")
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
