pluginManagement {
  repositories {
    gradlePluginPortal()
    maven("https://papermc.io/repo/repository/maven-public/")
  }
}

rootProject.name = "scoreboard-library-root"

include(":api")
include(":implementation")

include(":nms-base")
project(":nms-base").projectDir = file("versions/nms-base")

include(":packetevents")
project(":packetevents").projectDir = file("versions/packetevents")

include(":v1_19_R1")
project(":v1_19_R1").projectDir = file("versions/v1_19_R1")

include(":v1_8_R3")
project(":v1_8_R3").projectDir = file("versions/v1_8_R3")
