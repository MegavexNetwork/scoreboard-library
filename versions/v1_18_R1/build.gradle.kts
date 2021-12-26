plugins {
    id("net.megavex.scoreboardlibrary.nms-conventions")
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")

    compileOnly("com.github.ProtocolSupport:ProtocolSupport:05b7689664")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
}