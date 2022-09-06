package top.iseason.bukkit.bukkittemplate

open class KotlinPlugin {

    /**
     * 获取bukkit插件对象,在onLoad阶段才会被赋值
     */
    lateinit var javaPlugin: BukkitTemplate

    /**
     * 在其他线程加载，比onEnable先调用,结束了才调用onEnable
     */
    open fun onAsyncLoad() {}

    /**
     * 在插件启用后运行
     */
    open fun onEnable() {}

    /**
     * 在插件启用后异步运行
     */
    open fun onAsyncEnable() {}

    /**
     * 在插件停用时运行
     */
    open fun onDisable() {}


}