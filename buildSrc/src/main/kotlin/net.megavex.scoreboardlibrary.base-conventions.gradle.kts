import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
  `maven-publish`
  `java-library`
  id("net.kyori.indra")
  id("net.kyori.indra.publishing")
}

val libs = extensions.getByType(org.gradle.accessors.dm.LibrariesForLibs::class)

repositories {
  mavenCentral()
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
  compileOnly(libs.spigotApi)
  compileOnly(libs.bundles.adventure)

  testImplementation(libs.spigotApi)
  testImplementation(libs.bundles.adventure)
  testImplementation(libs.junitJupiterApi)
  testRuntimeOnly(libs.junitJupiterEngine)
}

indra {
  github("MegavexNetwork", "scoreboard-library") {
    ci(true)
  }
  mitLicense()

  javaVersions {
    target(8)
    minimumToolchain(17)
  }

  configurePublications {
    pom {
      url = "https://github.com/MegavexNetwork/agones-kt"
      developers {
        developer {
          id = "vytskalt"
          name = "vytskalt"
          email = "vytskalt@protonmail.com"
        }
      }
    }
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
}
