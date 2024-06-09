plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.buildIndra)
  implementation(libs.buildNmcp)
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
