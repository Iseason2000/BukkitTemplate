package com.example.bukkit.templateplugin

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.Lang
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key
import top.iseason.bukkittemplate.debug.info

@Key
@FilePath("lang.yml")
object Lang : Lang() {
    var hello_message = "你好 世界"
    var welcome_message = "欢迎来到我的世界"
    var quit_message = "玩家 %player% 已退出了服务器"
    var map = mapOf("sasas" to 11)
    var list = listOf("asdasdas", "asdasdasd")
    var set = setOf("asdasdas", "xcvzxvx")

    override fun onLoaded(section: ConfigurationSection) {
        super.onLoaded(section)
        info("语言文件已重载")
        info(hello_message)
    }
}