plugins {
  `java-library`
}

allprojects {
  version = "2.1.2"
  group = "net.megavex"
  description = "Powerful packet-level scoreboard library for Paper/Spigot servers"

  repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.papermc.io/repository/maven-public/")
  }
}

subprojects {
  apply(plugin = "java-library")

  dependencies {
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

    val adventureVersion = "4.16.0"
    compileOnly("net.kyori:adventure-api:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-gson:$adventureVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")

    testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    testImplementation("net.kyori:adventure-api:$adventureVersion")
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
}
