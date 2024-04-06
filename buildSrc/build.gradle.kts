plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.buildIndra)
  implementation(libs.buildNmcp)
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
