# Installation

I'm only going to show the installation for Gradle, however everything can be applied to Maven too.

First, add the repository:

```kotlin
repositories {
  maven("https://jitpack.io/")
}
```

Then go [here](https://jitpack.io/#MegavexNetwork/scoreboard-library) and find the latest version. Now, you can add the
dependencies:

```kotlin
dependencies {
    val libraryVersion = "..."
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:implementation:$libraryVersion") // Includes the API and the implementation

    // Add version implementations you want:
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_8_R3:$libraryVersion")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_19_R1:$libraryVersion")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:packetevents:$libraryVersion")

    // If using the PacketEvents implementation, scoreboard-library expects PacketEvents to be in the classpath.
    // Follow either of:
    // - https://github.com/retrooper/packetevents/wiki/Depending-on-pre-built-PacketEvents
    // - https://github.com/retrooper/packetevents/wiki/Shading-PacketEvents
  
    // If using the 1.8 version implementation, add Adventure as well:
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")
}
```

You will need to shade these dependencies and relocate them with something
like [Shadow](https://imperceptiblethoughts.com/shadow/).
