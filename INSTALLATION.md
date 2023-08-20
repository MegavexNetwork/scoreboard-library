# Installation

Latest version: `2.0.0-RC12`

## Gradle

Make sure you have the Jitpack repository:

```kotlin
repositories {
  maven("https://jitpack.io/")
}
```

Then add the dependencies:

```kotlin
dependencies {
  val scoreboardLibraryVersion = "{VERSION HERE}"
  implementation("com.github.megavexnetwork.scoreboard-library:scoreboard-library-api:$scoreboardLibraryVersion")
  runtimeOnly("com.github.megavexnetwork.scoreboard-library:scoreboard-library-implementation:$scoreboardLibraryVersion")
  implementation("com.github.megavexnetwork.scoreboard-library:scoreboard-library-extra-kotlin:$scoreboardLibraryVersion") // If using Kotlin

  // Add packet adapter implementations you want:
  runtimeOnly("com.github.megavexnetwork.scoreboard-library:scoreboard-library-modern:$scoreboardLibraryVersion") // 1.17+
  runtimeOnly("com.github.megavexnetwork.scoreboard-library:scoreboard-library-packetevents:$scoreboardLibraryVersion") // 1.8+
  runtimeOnly("com.github.megavexnetwork.scoreboard-library:scoreboard-library-v1_8_R3:$scoreboardLibraryVersion") // 1.8

  // If using the PacketEvents implementation, scoreboard-library expects PacketEvents to be in the classpath.
  // Follow either of:
  // - https://github.com/retrooper/packetevents/wiki/Depending-on-pre-built-PacketEvents
  // - https://github.com/retrooper/packetevents/wiki/Shading-PacketEvents
  // Example how to load PacketEvents in your plugin:
  // https://github.com/retrooper/packetevents-example/blob/aa20c0cee117c06d5f86e61d52f9ad4b22df97c0/shading-packetevents/src/main/java/main/Main.java

  // If using the 1.8 version implementation, add Adventure as well:
  implementation("net.kyori:adventure-platform-bukkit:4.0.1")
}
```

You will need to shade these dependencies and relocate them with something
like [Shadow](https://imperceptiblethoughts.com/shadow/).

## Maven

Make sure you have the Jitpack repository:
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io/</url>
  </repository>
</repositories>
```

Then add the dependencies:
```xml
<dependencies>
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-api</artifactId>
    <version>{VERSION HERE}</version>
  </dependency>
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-implementation</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <!-- If using Kotlin: -->
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-extra-kotlin</artifactId>
    <version>{VERSION HERE}</version>
  </dependency>

  <!-- Add packet adapter implementations you want: -->
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-modern</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-packetevents</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>
  <dependency>
    <groupId>com.github.megavexnetwork.scoreboard-library</groupId>
    <artifactId>scoreboard-library-v1_8_R3</artifactId>
    <version>{VERSION HERE}</version>
    <scope>runtime</scope>
  </dependency>

  <!--
    If using the PacketEvents implementation, scoreboard-library expects PacketEvents to be in the classpath.
    Follow either of:
    - https://github.com/retrooper/packetevents/wiki/Depending-on-pre-built-PacketEvents
    - https://github.com/retrooper/packetevents/wiki/Shading-PacketEvents
    Example how to load PacketEvents in your plugin:
    https://github.com/retrooper/packetevents-example/blob/aa20c0cee117c06d5f86e61d52f9ad4b22df97c0/shading-packetevents/src/main/java/main/Main.java 
    -->

  <!-- If using the 1.8 version implementation, add Adventure as well: -->
  <dependency>
    <groupId>net.kyori</groupId>
    <artifactId>adventure-platform-bukkit</artifactId>
    <version>4.0.1</version>
  </dependency>
</dependencies>
```

You will need to shade these dependencies and relocate them with something
like [maven-shade-plugin](https://maven.apache.org/plugins/maven-shade-plugin/).
