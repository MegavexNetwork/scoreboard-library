plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation(libs.buildIndra)
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
