plugins {
    id("net.megavex.scoreboardlibrary.nms-conventions")
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    compileOnly("org.spigotmc:spigot:1.18.1-R0.1-SNAPSHOT")
    compileOnly("com.github.ProtocolSupport:ProtocolSupport:05b7689664")
}