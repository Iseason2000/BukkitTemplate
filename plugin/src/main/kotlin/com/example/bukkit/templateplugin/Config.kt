package com.example.bukkit.templateplugin

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkittemplate.config.annotations.Comment
import top.iseason.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkittemplate.config.annotations.Key

@FilePath("config.yml")
object Config : SimpleYAMLConfig() {

    @Key("test.test1.test2")
    @Comment("", "6666")
    var test = 0

    @Key
    @Comment("", "list 测试")
    var test3 = mutableListOf("12312", "asdfas46", "tew4q5t456wefg6s")

    @Key
    @Comment("", "set 测试")
    var set = mutableSetOf("asdasd", "asdasdas", "asdas")

    @Comment("", "map 测试")
    @Key
    var map = mutableMapOf("test" to mutableMapOf("1" to mutableMapOf('1' to 2), "2" to "2"), "test2" to 2)

    override fun onLoaded(section: ConfigurationSection) {
        println("loaded")
    }

    override fun onSaved(section: ConfigurationSection) {
        println("saved")
    }
}