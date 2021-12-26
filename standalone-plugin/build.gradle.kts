plugins {
    id("net.megavex.scoreboardlibrary.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

dependencies {
    api(project(":implementation"))

    implementation(project(":v1_8_R3"))
    implementation(project(":v1_18_R1"))
}

bukkit {
    name = "ScoreboardLibrary"
    main = "ScoreboardLibraryPlugin"
    apiVersion = "1.18"
    description = "https://github.com/MegavexNetwork/scoreboard-library"
    author = "VytskaLT"
}

/*tasks {
    named<ShadowJar>("shadowJar") {
    }
}*/

tasks {
    build {
        dependsOn(shadowJar)
    }
}