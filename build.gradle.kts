plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.kyori.blossom") version "1.2.0"
}

group = "com.vendoau"

base {
    archivesName.set("forceversion-universal")
}

allprojects {
    apply(plugin = "net.kyori.blossom")
    apply(plugin = "java")

    repositories {
        mavenCentral()

        // ViaVersion
        maven("https://repo.viaversion.com")

        // Spigot
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")

        // Paper
        maven("https://repo.papermc.io/repository/maven-public")

        // Sponge
        maven("https://repo.spongepowered.org/repository/maven-public")
    }

    dependencies {
        implementation("net.kyori:adventure-text-minimessage:4.12.0")
        implementation("org.spongepowered:configurate-yaml:4.1.2")
        compileOnly("com.viaversion:viaversion-api:4.5.1")
    }

    blossom {
        replaceToken("\${version}", version)
        replaceToken("\${description}", description)
        replaceToken("\${website}", property("website"))
        replaceToken("\${source}", property("source"))
        replaceToken("\${issues}", property("issues"))
    }

    tasks.processResources {
        expand(project.properties)
    }
}

dependencies {
    implementation(project(":Common"))
    implementation(project(":Bukkit"))
    implementation(project(":Bungee"))
    implementation(project(":Sponge"))
    implementation(project(":Velocity"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    relocate("org.bstats", "com.vendoau.bstats")
}