base {
    archivesName.set("forceversion-bukkit")
}

dependencies {
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")
    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    compileOnly(project(":Common"))
}