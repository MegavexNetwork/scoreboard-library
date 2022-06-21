plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
}

repositories {
  maven {
    name = "SportPaper"
    url = uri("https://repo.ashcon.app/nexus/content/repositories/snapshots/")
  }
}

dependencies {
  compileOnly(project(":nms-base"))
  compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT")
}
