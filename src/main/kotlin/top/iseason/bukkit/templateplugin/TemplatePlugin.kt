package top.iseason.bukkit.templateplugin

import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.command.CommandBuilder
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.ui.UIListener

import top.iseason.bukkit.bukkittemplate.utils.toColor

object TemplatePlugin : KotlinPlugin() {

    override fun onAsyncLoad() {

//        command1()
//        command3()
        openUICommand()
    }

    override fun onEnable() {
        //如果使用命令模块，取消注释
        CommandBuilder.onEnable()

        //如果使用UI模块,取消注释
        UIListener.onEnable()

        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
        info("${ChatColor.GREEN}插件已启用!")
    }

    override fun onAsyncEnable() {
//        Config
    }

    override fun onDisable() {


        //如果使用命令模块，取消注释
        CommandBuilder.onDisable()

        //如果使用UI模块,取消注释
        UIListener.onDisable()

        //如果使用配置模块，取消注销
//        ConfigWatcher.onDisable()

        info("${ChatColor.YELLOW}插件已卸载!")
    }

}