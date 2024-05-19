plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.9.10"
}

group = "top.iseason.bukkittemplate"

val exposedVersion: String by rootProject
repositories {
    maven {
        name = "MMOItems"
        url = uri("https://nexus.phoenixdevt.fr/repository/maven-public/")
    }
    maven {
        name = "Oraxen"
        url = uri("https://repo.oraxen.com/releases")
    }
}
dependencies {
//    compileOnly("org.spigotmc:spigot-api:1.19.4-R0.1-SNAPSHOT")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.8.10")

    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1") { isTransitive = false }
    compileOnly("io.th0rgal:oraxen:1.175.0") { isTransitive = false }
}
tasks {
    compileJava {
        options.isFailOnError = false
        options.isWarnings = false
        options.isVerbose = false
        options.encoding = "UTF-8"
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    build {
        dependsOn(named("shadowJar"))
    }
    dokkaHtml.configure {
        dokkaSourceSets {
            named("main") {
                moduleName.set("BukkitTemplate")
            }
        }
    }
}
