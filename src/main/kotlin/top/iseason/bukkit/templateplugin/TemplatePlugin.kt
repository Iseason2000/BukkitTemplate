package top.iseason.bukkit.templateplugin

import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.debug.info

object TemplatePlugin : KotlinPlugin() {
    override fun onLoad() {
        Config
        Lang
        info("${ChatColor.YELLOW} 插件加载中...")
    }

    override fun onEnable() {
        info("${ChatColor.GREEN} 插件已启用!")
    }

    override fun onDisable() {
//        println("disable")
        info("${ChatColor.YELLOW} 插件已卸载!")
    }

}