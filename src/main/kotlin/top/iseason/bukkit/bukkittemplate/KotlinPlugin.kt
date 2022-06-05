package top.iseason.bukkit.bukkittemplate

import org.bukkit.plugin.java.JavaPlugin

abstract class KotlinPlugin {
    /**
     * 统计插件的ID
     */
    open var bstatsID = -1

    /**
     * 获取bukkit插件对象
     */
    lateinit var javaPlugin: JavaPlugin

    /**
     * 在插件加载时运行
     */
    abstract fun onLoad()

    /**
     * 在插件启用时运行
     */
    abstract fun onEnable()

    /**
     * 在插件停用时运行
     */
    abstract fun onDisable()

}