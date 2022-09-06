package top.iseason.bukkit.bukkittemplate.utils.bukkit

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitTask
import top.iseason.bukkit.bukkittemplate.utils.MessageUtils.toColor
import top.iseason.bukkit.bukkittemplate.utils.bukkit.EventUtils.listen
import top.iseason.bukkit.bukkittemplate.utils.bukkit.EventUtils.unregister
import top.iseason.bukkit.bukkittemplate.utils.submit

object IOUtils {
    /**
     * 打开一个界面，让玩家输入物品，当界面关闭时回调方法
     * @param inv 打开的界面
     * @param async 是否异步运行
     * @param onFinish 回调方法
     */
    fun Player.onItemInput(
        inv: Inventory = Bukkit.createInventory(null, 54, "&a请输入物品".toColor()),
        async: Boolean = false,
        onFinish: (Inventory) -> Unit
    ) {
        submit {
            openInventory(inv)
        }
        var task: BukkitTask? = null
        val listener = listen<InventoryCloseEvent> {
            if (inventory == inv) {
                submit(async = async) {
                    onFinish(inv)
                }
                task?.cancel()
                it.unregister()
            }
        }
        task = submit(delay = 12000) {
            if (openInventory.topInventory == inv) {
                closeInventory()
            } else {
                onFinish(inv)
                listener.unregister()
            }
        }
    }


}
