plugins {
  id("net.megavex.scoreboardlibrary.publish-conventions")
  id("net.megavex.scoreboardlibrary.nms-conventions")
}

repositories {
  maven {
    name = "SportPaper"
    url = uri("https://repo.ashcon.app/nexus/content/repositories/snapshots/")
  }
}

dependencies {
  compileOnly("app.ashcon:sportpaper:1.8.8-R0.1-SNAPSHOT")
}
