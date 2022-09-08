package top.iseason.bukkit.bukkittemplate.utils.bukkit

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import top.iseason.bukkit.bukkittemplate.BukkitTemplate

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
        crossinline action: T.(Listener) -> Unit
    ): TempListener {
        val tempListener = TempListener()
        tempListener.executor = EventExecutor { _, event ->
            runCatching {
                action.invoke(event as T, tempListener)
            }.getOrElse { it.printStackTrace() }
        }
        Bukkit.getPluginManager()
            .registerEvent(
                T::class.java, tempListener, priority,
                tempListener.executor, BukkitTemplate.getPlugin(), ignoreCancelled
            )
        return tempListener
    }

    class TempListener : Listener {
        lateinit var executor: EventExecutor
    }
}