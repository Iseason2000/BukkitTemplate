import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

val kotlinVersion = "1.6.21"
plugins {
    kotlin("jvm") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

// 插件名称，在settings.gradle.kts 修改
val pluginName = rootProject.name
//包名
group = "top.iseason.bukkit.${pluginName.toLowerCaseAsciiOnly()}"
// 作者
val author = "Iseason"
// jar包输出路径
val jarOutputFile = "E:\\mc\\1.18 server\\plugins"
//插件版本
version = "1.0-SNAPSHOT"

val groupS = group
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "spigot"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8"))
//    implementation(kotlin("reflect"))
    implementation("org.bstats:bstats-bukkit:3.0.0")
//    implementation("com.github.ReflxctionDev:PluginLib:1.3")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")

}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    shadowJar {
        mergeServiceFiles()
        relocate("org.bstats", "$groupS.lib.bstats")
        relocate("top.iseason.bukkit.bukkittemplate", "$groupS.core")
        minimize()
        destinationDirectory.set(file(jarOutputFile))
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "main" to "$groupS.core.TemplatePlugin",
                "name" to pluginName,
                "version" to project.version,
                "author" to author,
                "kotlinVersion" to kotlinVersion,
            )
        }
    }
}
tasks.named<Jar>("jar") {
    includeEmptyDirs = false
}
