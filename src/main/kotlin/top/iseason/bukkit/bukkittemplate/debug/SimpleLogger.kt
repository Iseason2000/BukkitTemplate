package top.iseason.bukkit.bukkittemplate.debug

import org.bukkit.Bukkit
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger.isDebugEnabled
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger.prefix
import top.iseason.bukkit.bukkittemplate.utils.toColor

object SimpleLogger {
    var isDebugEnabled: Boolean = true
    var prefix: String? = null
        get() = field ?: "[${TemplatePlugin.getPlugin().name}] "
}

fun info(message: Any?) {
    Bukkit.getLogger().info("$prefix${message.toString()}".toColor())
}

fun debug(message: Any?) {
    if (!isDebugEnabled) return
    Bukkit.getLogger().info("$prefix${message.toString()}".toColor())
}

fun warn(message: Any?) {
    Bukkit.getLogger().warning("$prefix${message.toString()}".toColor())
}
