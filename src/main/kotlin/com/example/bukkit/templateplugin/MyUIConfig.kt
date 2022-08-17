package com.example.bukkit.templateplugin

import org.bukkit.configuration.ConfigurationSection
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.ui.container.UIContainer

@FilePath("myui.yml")
object MyUIConfig : SimpleYAMLConfig() {
    var myUI: UIContainer? = null
    override val onLoaded: (ConfigurationSection.() -> Unit) = {
        myUI = MultiUI.deserialize(config)
    }
}