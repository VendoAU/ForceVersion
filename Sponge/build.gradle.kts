import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

base {
    archivesName.set("forceversion-sponge")
}

dependencies {
    implementation("org.bstats:bstats-sponge:3.0.0")
    compileOnly("org.spongepowered:spongeapi:8.1.0")
    compileOnly("com.viaversion:viaversion-api:4.5.1")
    compileOnly(project(":Common"))
}

sponge {
    apiVersion("8.1.0")
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("forceversion") {
        displayName("ForceVersion")
        entrypoint("com.vendoau.forceversion.ForceVersionSponge")
        description(property("description").toString())
        links {
            homepage(property("website").toString())
            source(property("source").toString())
            issues(property("issues").toString())
        }
        contributor("VendoAU") {
            description("Lead Developer")
        }
        dependency("viaversion") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            version("4.5.1")
            optional(false)
        }
    }
}