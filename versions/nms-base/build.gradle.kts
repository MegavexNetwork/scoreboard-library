plugins {
    id("net.megavex.scoreboardlibrary.publish-conventions")
}

dependencies {
    api(project(":api"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-XDignore.symbol.file")
}