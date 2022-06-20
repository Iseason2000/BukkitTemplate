package top.iseason.bukkit.bukkittemplate.persistence.config.annotations

/**
 * 添加注释
 */
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Target(AnnotationTarget.FIELD)
annotation class Comment(vararg val value: String)