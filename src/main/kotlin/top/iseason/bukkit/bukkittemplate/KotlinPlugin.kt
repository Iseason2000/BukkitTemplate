package top.iseason.bukkit.bukkittemplate

abstract class KotlinPlugin {
    /**
     * 统计插件的ID
     */
    open var bstatsID = -1

    /**
     * 获取bukkit插件对象,在onLoad阶段才会被赋值
     */
    lateinit var javaPlugin: TemplatePlugin

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

    /**
     * 在插件主类初始化之后调用,此时插件尚未加载
     */
    abstract fun init()
}