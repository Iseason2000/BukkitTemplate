package top.iseason.bukkit.templateplugin

import org.bukkit.ChatColor
import top.iseason.bukkit.bukkittemplate.KotlinPlugin
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.utils.toColor

object TemplatePlugin : KotlinPlugin() {

    override fun onAsyncLoad() {
        command1()
    }

    override fun onEnable() {
        SimpleLogger.prefix = "&a[&6${javaPlugin.description.name}&a]&r ".toColor()
        info("${ChatColor.GREEN}插件已启用!")
//        CommandBuilder.simpleCommandMap.register("iseason", object : Command("iseason") {
//            override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
//                println("iseason")
//                return true
//            }
//        })

//        command2()
    }

    override fun onAsyncEnable() {
    }

    override fun onDisable() {
        info("${ChatColor.YELLOW}插件已卸载!")
    }

}