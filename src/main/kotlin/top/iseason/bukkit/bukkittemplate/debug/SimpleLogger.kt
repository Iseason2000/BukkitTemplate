package top.iseason.bukkit.bukkittemplate.debug

import org.bukkit.Bukkit
import top.iseason.bukkit.bukkittemplate.SimpleLogger
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage

fun info(message: Any?) {
    Bukkit.getConsoleSender().sendColorMessage(SimpleLogger.prefix + message)
}

fun debug(message: Any?) {
    TemplatePlugin.getPlugin().simpleLogger.debug(message.toString())
}

fun warn(message: Any?) {
    TemplatePlugin.getPlugin().simpleLogger.warning(message.toString())
}
