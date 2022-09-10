package top.iseason.bukkit.bukkittemplate.utils.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import top.iseason.bukkit.bukkittemplate.BukkitTemplate

/**
 * bukkit的事件相关工具
 */
object EventUtils {
    /**
     * 快速注册监听器
     */
    fun Listener.register() {
        Bukkit.getPluginManager().registerEvents(this, BukkitTemplate.getPlugin())
    }

    /**
     * 快速注销监听器
     */
    fun Listener.unregister() {
        HandlerList.unregisterAll(this)
    }

    /**
     * 通过方法快速注册一个事件监听器
     */
    inline fun <reified T : Event> listen(
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        noinline action: T.(Listener) -> Unit
    ): TempListener {
        return createListener(T::class.java, priority, ignoreCancelled, action)
    }

    class TempListener : Listener {
        lateinit var executor: EventExecutor
    }

    /**
     * 根据class创建事件监听器
     */
    fun <E : Event> createListener(
        clazz: Class<E>,
        priority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = true,
        action: E.(Listener) -> Unit
    ): TempListener {
        val tempListener = TempListener()
        tempListener.executor = EventExecutor { _, event ->
            runCatching {
                action.invoke(event as E, tempListener)
            }.getOrElse { it.printStackTrace() }
        }
        Bukkit.getPluginManager()
            .registerEvent(
                clazz, tempListener, priority,
                tempListener.executor, BukkitTemplate.getPlugin(), ignoreCancelled
            )
        return tempListener
    }
}