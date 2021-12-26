rootProject.name = "scoreboard-library"

include(":api")
include(":implementation")
include(":standalone-plugin")

include(":nms-base")
project(":nms-base").projectDir = file("versions/nms-base")

include(":v1_18_R1")
project(":v1_18_R1").projectDir = file("versions/v1_18_R1")

include(":v1_8_R3")
project(":v1_8_R3").projectDir = file("versions/v1_8_R3")