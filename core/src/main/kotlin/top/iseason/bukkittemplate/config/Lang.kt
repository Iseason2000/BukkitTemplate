package top.iseason.bukkittemplate.config

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.debug.SimpleLogger
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils

open class Lang(
    defaultPath: String? = null,
    isAutoUpdate: Boolean = true,
    updateNotify: Boolean = true
) : SimpleYAMLConfig(defaultPath, isAutoUpdate, updateNotify) {
    @Comment(
        "",
        "消息留空将不会显示，使用 '\\n' 或换行符 可以换行",
        "支持 & 颜色符号，1.17以上支持16进制颜色代码，如 #66ccff",
        "{0}、{1}、{2}、{3} 等格式为该消息独有的变量占位符",
        "所有消息支持PlaceHolderAPI",
        "以下是一些特殊消息, 大小写不敏感，可以通过 \\n 自由组合",
        "以 [Broadcast] 开头将以广播的形式发送，支持BungeeCord",
        "以 [Actionbar] 开头将发送ActionBar消息",
        "以 [Main-Title] 开头将发送大标题消息",
        "以 [Sub-Title] 开头将发送小标题消息",
        "以 [Command] 开头将以消息接收者的身份运行命令",
        "以 [Console] 开头将以控制台的身份运行命令",
        "以 [OP-Command] 开头将赋予消息接收者临时op运行命令 (慎用)"
    )
    var readme = ""

    @Comment("", "消息前缀")
    var msg_prefix = MessageUtils.defaultPrefix

    @Comment("", "日志前缀")
    var log_prefix = MessageUtils.defaultPrefix

    @Comment("", "是否使用 minimessage 模式, 将会自动下载依赖")
    var mini_message = false

    override fun onLoaded(section: ConfigurationSection) {
        if (mini_message) MessageUtils.enableMiniMessage()
        else MessageUtils.disableMiniMessage()
        MessageUtils.defaultPrefix = msg_prefix
        SimpleLogger.prefix = log_prefix
    }
}