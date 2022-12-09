# Installation

I'm only going to show the installation for Gradle, however everything can be applied to Maven too.

First, add the Jitpack repository:

```kotlin
repositories {
  maven("https://jitpack.io/")
}
```

Then go [here](https://jitpack.io/#MegavexNetwork/scoreboard-library) and find the latest version. Now, you can add the
dependencies:

```kotlin
dependencies {
    val scoreboardLibraryVersion = "..."
    implementation("com.github.MegavexNetwork.scoreboard-library:api:$scoreboardLibraryVersion")
    implementation("com.github.MegavexNetwork.scoreboard-library:extra-kotlin:$scoreboardLibraryVersion") // If using Kotlin
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:implementation:$scoreboardLibraryVersion")

    // Add version implementations you want:
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_8_R3:$scoreboardLibraryVersion")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_19_R1:$scoreboardLibraryVersion")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:packetevents:$scoreboardLibraryVersion")

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
