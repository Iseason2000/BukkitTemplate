package top.iseason.bukkit.bukkittemplate.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * 发送带颜色转换的消息,不输出null与空string
 */
fun CommandSender.sendColorMessage(message: Any?) {
    if (message == null || message.toString().isEmpty()) return
    sendMessage(message.toString().toColor())
}

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(vararg messages: String) = messages.forEach { sendColorMessage(it) }

/**
 * 发送带颜色转换的消息
 */
fun CommandSender.sendColorMessages(messages: Collection<String>?) = messages?.forEach { sendColorMessage(it) }

/**
 * 进行颜色转换并发送给所有人
 */
fun broadcast(message: Any?) = Bukkit.getOnlinePlayers().forEach { it.sendColorMessage(message) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(message: Any?) = Bukkit.getConsoleSender().sendColorMessage(message)

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Collection<String>?) = messages?.forEach { sendConsole(it) }

/**
 * 进行颜色转换并发送给控制台
 */
fun sendConsole(messages: Array<String>?) = messages?.forEach { sendConsole(it) }

/**
 * 将String转为bukkit支持的颜色消息
 */
fun String.toColor() = ChatColor.translateAlternateColorCodes('&', this)