rootProject.name = "scoreboard-library"

include(":api")
include(":commons")
include(":implementation")
include(":extra-kotlin")

include(":packet-adapter-base")
project(":packet-adapter-base").projectDir = file("versions/packet-adapter-base")

include(":packetevents")
project(":packetevents").projectDir = file("versions/packetevents")

include(":modern")
project(":modern").projectDir = file("versions/modern")

include(":v1_8_R3")
project(":v1_8_R3").projectDir = file("versions/v1_8_R3")

val modulePrefix = rootProject.name
rootProject.children.forEach {
  it.name = "$modulePrefix-${it.name}"
}
