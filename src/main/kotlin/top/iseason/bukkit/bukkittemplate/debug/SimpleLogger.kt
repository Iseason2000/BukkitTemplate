package top.iseason.bukkit.bukkittemplate.debug

import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.toColor

fun info(message: Any?) {
    TemplatePlugin.getPlugin().logger.info(message.toString().toColor())
}

fun debug(message: Any?) {
    TemplatePlugin.getPlugin().logger.debug(message.toString().toColor())
}

fun warn(message: Any?) {
    TemplatePlugin.getPlugin().logger.warning(message.toString().toColor())
}
