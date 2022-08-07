package top.iseason.bukkit.templateplugin

import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.utils.toColor

object TemplatePlugin : KotlinPlugin() {

    override fun onAsyncLoad() {
//        command1()
//        openUICommand()
    }

    override fun onEnable() {
        //如果使用命令模块，取消注释
//        CommandBuilder.onEnable()
//
//        //如果使用UI模块,取消注释
//        UIListener.onEnable()

        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
        info("&a插件已启用!")
    }

    override fun onAsyncEnable() {

//        SimpleYAMLConfig.notifyMessage = "&7配置文件 &6%s &7已重载!"
//        Config.load(false)
//        LagCatcher.performanceCheck("test", 0) {
//            DependencyDownloader().addRepositories("https://maven.aliyun.com/repository/public")
//                .downloadDependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
//        }
    }

    override fun onDisable() {

        //如果使用命令模块，取消注释
//        CommandBuilder.onDisable()
//
//        //如果使用UI模块,取消注释
//        UIListener.onDisable()
//
//        //如果使用配置模块，取消注销
//        ConfigWatcher.onDisable()

        info("&6插件已卸载!")
    }

}