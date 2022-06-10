plugins {
  `java-library`
  `maven-publish`
}

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://oss.sonatype.org/content/groups/public/")
  maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
  compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

  val adventureVersion = "4.11.0"
  compileOnly("net.kyori:adventure-api:$adventureVersion")
  compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
  compileOnly("net.kyori:adventure-text-serializer-gson:$adventureVersion")
}

group = "net.megavex.scoreboardlibrary"
version = "1.0.0"

tasks {
  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
  }

  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }

  processResources {
    filteringCharset = Charsets.UTF_8.name()
  }
}
