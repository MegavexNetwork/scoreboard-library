plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  compileOnly(files(libs::class.java.protectionDomain.codeSource.location))
}
