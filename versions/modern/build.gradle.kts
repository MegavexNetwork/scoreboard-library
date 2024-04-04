plugins {
  id("net.megavex.scoreboardlibrary.base-conventions")
  alias(libs.plugins.paperweight)
}

dependencies {
  compileOnly(project(":scoreboard-library-packet-adapter-base"))
  paperweight.paperDevBundle(libs.versions.devBundle.get())
}

tasks {
  compileJava {
    // Workaround to get it to compile targeting Java 8 while using NMS which targets 17
    // Why does this work? I have no idea
    options.release = null
  }

  // Based on https://github.com/unnamed/hephaestus-engine/blob/db6deabe1ddb0a549306b0c4b519c3e79e6f1ea8/runtime-bukkit/adapt-v1_20_R3/build.gradle.kts
  reobfJar {
    outputJar = file("build/libs/scoreboard-library-modern-$version-reobf.jar")
  }

  assemble {
    dependsOn(reobfJar)
  }

  create<Sign>("signReobfJar") {
    dependsOn(reobfJar)
    description = "Signs the reobfuscated modern packet adapter jar"
    val signature = Signature(
      { reobfJar.get().outputJar.get().asFile },
      { "reobf" },
      this,
      this
    )
    signatures += signature
    outputs.files(signature.file)
  }
}

java {
  disableAutoTargetJvm()
}

indra {
  includeJavaSoftwareComponentInPublications(false)
}

publishing {
  publications.getByName<MavenPublication>("maven") {
    artifact(tasks.reobfJar)

    artifact(tasks.named("signReobfJar")) {
      classifier = "reobf"
      extension = "asc"
    }
  }
}
