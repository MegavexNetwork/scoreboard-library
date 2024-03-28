plugins {
  `maven-publish`
  id("net.megavex.scoreboardlibrary.base-conventions")
  alias(libs.plugins.paperweight)
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  paperweight.paperDevBundle(libs.versions.devBundle.get())
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
