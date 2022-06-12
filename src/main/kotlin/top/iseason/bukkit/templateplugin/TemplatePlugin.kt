package top.iseason.bukkit.templateplugin

import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.utils.toColor

object TemplatePlugin : KotlinPlugin() {

    override fun init() {
        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
        //屏蔽默认的插件启用信息
        SimpleLogger.addFilter("Loading " + javaPlugin.description.fullName)
        SimpleLogger.addFilter("Enabling " + javaPlugin.description.fullName)
        SimpleLogger.addFilter("Disabling " + javaPlugin.description.fullName)
    }

    override fun onLoad() {
        info("${ChatColor.YELLOW}插件加载中")
    }

    override fun onEnable() {
//        如果使用UI包取消注销
//        UIListener
        info("${ChatColor.GREEN}插件已启用!")
        command1()
//        command2()
    }

    override fun onDisable() {
//        如果使用UI包取消注销
//        UIListener.onDisable()
        info("${ChatColor.YELLOW}插件已卸载!")
    }

}