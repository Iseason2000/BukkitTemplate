package top.iseason.bukkit.bukkittemplate.core.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory


/**
 * 新建一个UI的对象
 */
inline fun <reified T : BaseUI> buildUI(builder: T.() -> Unit = {}): Inventory {
    return T::class.java.newInstance().also(builder).inventory
}

/**
 * 打开某类UI，必要时可以修改
 */
inline fun <reified T : BaseUI> Player.openUI(builder: T.() -> Unit = {}) {
    try {
        openInventory(buildUI(builder))
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
}

//fun test() {
//    buildUI<BaseUI> {
//    }
//    Bukkit.getPlayer("Iseason")?.openUI<BaseUI>()
//}