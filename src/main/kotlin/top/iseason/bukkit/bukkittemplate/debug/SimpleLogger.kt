
package top.iseason.bukkit.bukkittemplate.core.debug

import top.iseason.bukkit.bukkittemplate.plugin

var isDebugEnabled: Boolean = true
fun info(message: Any?) {
    plugin.logger.info(message.toString())
}

fun debug(message: Any?) {
    if (!isDebugEnabled) return
    plugin.logger.info(message.toString())
}

fun warn(message: Any?) {
    plugin.logger.warning(message.toString())
}
