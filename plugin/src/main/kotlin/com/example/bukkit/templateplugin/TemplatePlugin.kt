package com.example.bukkit.templateplugin

import top.iseason.bukkittemplate.BukkitPlugin
import top.iseason.bukkittemplate.debug.info

@Suppress("UNUSED")
object TemplatePlugin : BukkitPlugin {

    override fun onEnable() {
        Lang.load()
//        command1()
////        //如果使用命令模块，取消注释
//        CommandHandler.updateCommands()
//        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
    }

    override fun onAsyncEnable() {

//        Config.load()
//        openUICommand()
//        UIListener.register()
        //命令
//        openUICommand()

        //如果使用UI模块,取消注释
//        UIListener.register()
        //使用数据库请取消注释以下2行
//        DatabaseConfig.load(false)
//        DatabaseConfig.initTables()
//        SimpleYAMLConfig.notifyMessage = "&7配置文件 &6%s &7已重载!"
//        Config.load(false)
//        LagCatcher.performanceCheck("test", 0) {
//            DependencyDownloader().addRepositories("https://maven.aliyun.com/repository/public")
//                .downloadDependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//        }

        info("&a插件已启用!")
    }

    override fun onDisable() {
        info("&6插件已卸载!")
    }

}