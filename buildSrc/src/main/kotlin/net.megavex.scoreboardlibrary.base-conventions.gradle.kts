import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
  `java-library`
}

// expose version catalog
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

tasks.withType<JavaCompile>().configureEach {
  sourceCompatibility = JavaVersion.VERSION_1_8.toString()
  targetCompatibility = JavaVersion.VERSION_1_8.toString()
  options.encoding = "UTF-8"
  options.isIncremental = true
  options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
}

tasks.test {
  useJUnitPlatform()
}
