plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath("com.guardsquare:proguard-gradle:7.2.2")
    }
}
subprojects {
    group = rootProject.group
    version = rootProject.version
    apply {
        plugin<com.github.jengelman.gradle.plugins.shadow.ShadowPlugin>()
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
    }
    repositories {
//    阿里的服务器速度快一点
        maven {
            name = "aliyun"
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        google()
        mavenCentral()
        maven {
            name = "spigot"
            url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/")
        }
        maven {
            name = "jitpack"
            url = uri("https://jitpack.io")
        }
        maven {
            name = "CodeMC"
            url = uri("https://repo.codemc.org/repository/maven-public")
        }
        mavenLocal()
    }

    dependencies {
        //基础库
        compileOnly(kotlin("stdlib-jdk8"))
//    反射库
//    compileOnly(kotlin("reflect"))

//    协程库
//    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
        // 本地依赖放在libs文件夹内
        compileOnly(fileTree("libs") { include("*.jar") })
    }
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
repositories {
//    阿里的服务器速度快一点
    maven {
        name = "aliyun"
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    google()
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    mavenLocal()
}
dependencies {
    //基础库
    compileOnly(kotlin("stdlib-jdk8"))
}