# Installation

I'm only going to show the installation for Gradle, however everything can be applied to Maven too.

First, add the [Jitpack](https://jitpack.io/) repository:

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
    implementation("com.github.MegavexNetwork.scoreboard-library:implementation:$libraryVersion") // Includes the API and the implementation

    // I want this plugin to support 1.8 & 1.19, so I'll add both version implementations:
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_8_R3:$libraryVersion")
    runtimeOnly("com.github.MegavexNetwork.scoreboard-library:v1_19_R1:$libraryVersion")

    // If you're targeting a server that doesn't have native Adventure support (1.8 or Spigot 1.19),
    // you will need to add Adventure too:
    implementation("net.kyori:adventure-platform-bukkit:4.0.1")
}
```

You will need to shade these dependencies and relocate them with something
like [Shadow](https://imperceptiblethoughts.com/shadow/).

Then add `ProtocolSupport` as a softdepend in your plugin.yml:
```yaml
softdepend: [ ProtocolSupport ]
```
