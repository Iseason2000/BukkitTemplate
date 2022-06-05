package top.iseason.bukkit.bukkittemplate.config.annotations

/**
 * 指定配置键值
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Key(val key: String = "")
