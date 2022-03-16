plugins {
  id("net.megavex.scoreboardlibrary.java-conventions")
  `maven-publish`
}

publishing {
  publications.create<MavenPublication>("maven") {
    from(components["java"])
  }
}
