plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

// 插件名称，请在gradle.properties 修改
val pluginName = getProperties("pluginName")

//包名，请在gradle.properties 修改
group = getProperties("groupId")
// 作者，请在gradle.properties 修改
val author = getProperties("author")
// jar包输出路径，请在gradle.properties 修改
val jarOutputFile = getProperties("jarOutputFile")
//插件版本，请在gradle.properties 修改
version = getProperties("version")

val groupS = group

repositories {
//    阿里的服务器速度快一点
    maven {
        name = "aliyun"
        url = uri("https://maven.aliyun.com/repository/public/")
    }
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
    //基础库
    compileOnly(kotlin("stdlib-jdk8"))
//    反射库
//    compileOnly(kotlin("reflect"))

//    协程库
//    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    implementation("org.bstats:bstats-bukkit:3.0.0")
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

}


tasks {
    shadowJar {
        minimize()
        relocate("org.bstats", "$groupS.lib.bstats")
        relocate("top.iseason.bukkit.bukkittemplate", "$groupS.lib.core")
        destinationDirectory.set(file(jarOutputFile))
        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    processResources {
        filesMatching("plugin.yml") {
            expand(
                "main" to "$groupS.core.TemplatePlugin",
                "name" to pluginName,
                "version" to project.version,
                "author" to author,
                "kotlinVersion" to getProperties("kotlinVersion"),
            )
        }
    }

}


fun getProperties(properties: String) = rootProject.properties[properties].toString()
