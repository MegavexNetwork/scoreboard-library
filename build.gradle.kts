plugins {
  `java-library`
}

allprojects {
  version = "2.0.0-RC9"
  group = "net.megavex"
  description = "Powerful packet-level sidebar/teams library for Paper/Spigot servers"

  repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}

subprojects {
  apply(plugin = "java-library")

  dependencies {
    compileOnly("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")
    testImplementation("dev.folia:folia-api:1.19.4-R0.1-SNAPSHOT")

    val adventureVersion = "4.14.0"
    compileOnly("net.kyori:adventure-api:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-gson:$adventureVersion")
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
    options.encoding = "UTF-8"
    options.isIncremental = true
    options.compilerArgs = mutableListOf("-Xlint:-deprecation,-unchecked")
  }

  tasks.test {
    useJUnitPlatform()
  }
}
