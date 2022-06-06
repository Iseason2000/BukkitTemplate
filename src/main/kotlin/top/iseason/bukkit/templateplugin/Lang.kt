package top.iseason.bukkit.templateplugin

import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.config.annotations.Key

@Key
@FilePath("lang.yml")
object Lang : SimpleYAMLConfig() {
    var hello_message = "你好 世界"
    var welcome_message = "欢迎来到我的世界"
    var quit_message = "玩家 %player% 已退出了服务器"
}