plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka") version "1.7.10"
}

group = "top.iseason.bukkittemplate"

val exposedVersion: String by rootProject

dependencies {
    // 数据库
    compileOnly("org.jetbrains.exposed:exposed-core:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    compileOnly("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    compileOnly("com.zaxxer:HikariCP:4.0.3")

    implementation("org.bstats:bstats-bukkit:3.0.0")
    implementation("io.github.bananapuncher714:nbteditor:7.18.3")
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")

    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.10")
}
tasks {
    compileJava {
        options.isFailOnError = false
        options.isWarnings = false
        options.isVerbose = false
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