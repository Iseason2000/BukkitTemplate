package top.iseason.bukkit.templateplugin

import org.bukkit.configuration.file.FileConfiguration
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.persistence.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.persistence.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.persistence.config.annotations.Key

@Key
@FilePath("lang.yml")
object Lang : SimpleYAMLConfig(updateNotify = false) {
    var hello_message = "你好 世界"
    var welcome_message = "欢迎来到我的世界"
    var quit_message = "玩家 %player% 已退出了服务器"
    override val onLoaded: (FileConfiguration.() -> Unit) = {
        info("语言文件已重载")
    }
}