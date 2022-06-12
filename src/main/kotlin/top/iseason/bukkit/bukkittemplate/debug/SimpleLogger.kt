package top.iseason.bukkit.bukkittemplate.debug

import org.bukkit.Bukkit
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage

fun info(message: Any?) {
    Bukkit.getConsoleSender().sendColorMessage(SimpleLogger.prefix + message)
}

fun debug(message: Any?) {
    if (SimpleLogger.isDebug)
        info(message)
}

fun warn(message: Any?) {
    TemplatePlugin.getPlugin().logger.warning(message.toString())
}

object SimpleLogger {
    var prefix = "[${TemplatePlugin.getPlugin().description.name}] "
    var isDebug = false
}
