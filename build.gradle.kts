import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

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
//        destinationDirectory.set(file(jarOutputFile))
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
                "main" to "$groupS.lib.core.TemplatePlugin",
                "name" to pluginName,
                "version" to project.version,
                "author" to author,
                "kotlinVersion" to getProperties("kotlinVersion")
            )
        }
    }
    task("buildAll") {
        dependsOn(shadowJar)
        doLast {
            val file = File(shadowJar.get().destinationDirectory.get().asFile, "${project.name}-${project.version}.jar")
            val fileOut = File(jarOutputFile, "${project.name}-${project.version}.jar")
            if (!file.exists()) return@doLast
            //负责删除遗留的空文件夹和复制jar包到输出路径
            removeEmpty(file, fileOut)
        }
    }
}

fun getProperties(properties: String) = rootProject.properties[properties].toString()

/**
 * 从jar包中删除空文件夹
 * @param jarInFileName jar包路径
 * @param jarOutFileName 输出路径
 */
fun removeEmpty(jarInFileName: File, jarOutFileName: File) {
    var entry: ZipEntry?
    val zis: ZipInputStream?
    var jos: JarOutputStream? = null
    var fis: FileInputStream? = null
    if (!jarInFileName.exists()) return
    try {
        fis = FileInputStream(jarInFileName)
        zis = ZipInputStream(fis)
        jos = JarOutputStream(FileOutputStream(jarOutFileName))
        val entries = mutableListOf<ZipEntry?>()
        val bytes = mutableListOf<ByteArray>()
        while (zis.nextEntry.also { entry = it } != null) {
            entries.add(entry!!)
            bytes.add(zis.readBytes())
        }
        for (i in (entries.size - 1) downTo 0) {
            val zipEntry = entries[i] ?: continue
            var hasNext = false
            for (i2 in (entries.size - 1) downTo 0) {
                val zipEntry2 = entries[i2] ?: continue
                if (zipEntry2.name.startsWith(zipEntry.name) && zipEntry2.name != zipEntry.name) {
                    hasNext = true
                    break
                }
            }
            if (!hasNext && !zipEntry.isDirectory) {
                jos.putNextEntry(zipEntry)
                jos.write(bytes[i])
                entries[i] = null
            }
        }

    } catch (ex: Exception) {
        throw IOException("unable to filter jar:" + ex.message)
    } finally {
        fis?.close()
        jos?.close()
    }
}
