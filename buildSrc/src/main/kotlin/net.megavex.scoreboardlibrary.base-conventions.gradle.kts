import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
  `maven-publish`
  `java-library`
  id("net.kyori.indra")
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
  javaVersions {
    target(8)
    minimumToolchain(17)
  }
}

tasks.withType<JavaCompile>().configureEach {
  options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
}
