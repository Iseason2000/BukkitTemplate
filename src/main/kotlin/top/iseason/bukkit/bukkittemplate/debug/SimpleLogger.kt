package top.iseason.bukkit.bukkittemplate.debug

import top.iseason.bukkit.bukkittemplate.TemplatePlugin


var isDebugEnabled: Boolean = true
fun info(message: Any?) {
    TemplatePlugin.getPlugin().logger.info(message.toString())
}

fun debug(message: Any?) {
    if (!isDebugEnabled) return
    TemplatePlugin.getPlugin().logger.info(message.toString())
}

fun warn(message: Any?) {
    TemplatePlugin.getPlugin().logger.warning(message.toString())
}
