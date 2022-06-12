package top.iseason.bukkit.bukkittemplate

abstract class KotlinPlugin {

    /**
     * 获取bukkit插件对象,在onLoad阶段才会被赋值
     */
    lateinit var javaPlugin: TemplatePlugin

    /**
     * 在插件启用时运行
     */
    abstract fun onEnable()

    /**
     * 在插件停用时运行
     */
    abstract fun onDisable()

}