plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "top.iseason.bukkittemplate"

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
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.20")

    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.3.2")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    compileOnly("net.Indyuce:MMOItems-API:6.9.4-SNAPSHOT") { isTransitive = false }
    compileOnly("com.github.LoneDev6:api-itemsadder:3.6.1") { isTransitive = false }
    compileOnly("io.th0rgal:oraxen:1.175.0") { isTransitive = false }
}
tasks {
    kotlin {
        jvmToolchain(8)
    }

    compileJava {
        options.encoding = "UTF-8"
        options.isFailOnError = false
    }
    dokkaHtml.configure {
        dokkaSourceSets {
            named("main") {
                moduleName.set("BukkitTemplate")
            }
        }
    }
}
