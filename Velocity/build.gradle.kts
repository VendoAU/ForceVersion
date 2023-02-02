base {
    archivesName.set("forceversion-velocity")
}

dependencies {
    implementation("org.bstats:bstats-velocity:3.0.0")
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
    compileOnly(project(":Common"))
}