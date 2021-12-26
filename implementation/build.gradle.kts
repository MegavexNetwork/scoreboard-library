plugins {
    id("net.megavex.scoreboardlibrary.publish-conventions")
}

dependencies {
    api(project(":api"))

    implementation(project(":nms-base"))
    compileOnly(project(":v1_8_R3"))
    compileOnly(project(":v1_18_R1"))
}