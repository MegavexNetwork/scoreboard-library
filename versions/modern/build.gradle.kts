plugins {
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

  compileJava {
    // Workaround to get it to compile targeting Java 8 while using NMS which targets 17
    // Why does this work? I have no idea
    options.release = null
  }
}

java {
  disableAutoTargetJvm()
}

publishing {
  publications.create<MavenPublication>("maven") {
    artifact(tasks.reobfJar)
  }
}

indra {
  includeJavaSoftwareComponentInPublications(false)
}
