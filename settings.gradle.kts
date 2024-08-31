pluginManagement {
    //kotlin 版本
    val kotlinVersion: String by settings
    //shadowJar 版本
    val shadowJarVersion: String by settings
    plugins {
        kotlin("jvm") version kotlinVersion
        id("com.gradleup.shadow") version shadowJarVersion
    }
}

val pluginName: String by settings

rootProject.name = pluginName

include("core", "plugin")

