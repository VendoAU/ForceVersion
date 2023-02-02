base {
    archivesName.set("forceversion-bungee")
}

dependencies {
    implementation("org.bstats:bstats-bungeecord:3.0.0")
    implementation("net.kyori:adventure-platform-bungeecord:4.2.0")
    compileOnly("net.md-5:bungeecord-api:1.19-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
}