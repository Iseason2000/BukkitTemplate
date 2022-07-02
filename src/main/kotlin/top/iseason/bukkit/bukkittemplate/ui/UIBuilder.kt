package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory


/**
 * 新建一个UI的对象
 */
inline fun <reified T : BaseUI> buildUI(builder: T.() -> Unit = {}): Inventory {
    return T::class.java.newInstance().also(builder).inventory
}

/**
 * 打开某类UI，必要时可以修改,使用此api请确保该类有空构造函数
 */
inline fun <reified T : BaseUI> Player.openUI(builder: T.() -> Unit = {}) {
    try {
        openInventory(buildUI(builder))
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
}

/**
 * 打开某类UI，必要时可以修改,使用此api请确保该类有空构造函数
 */
inline fun <reified T : PageableUI> Player.openPageableUI(builder: T.() -> Unit = {}) {
    try {
        T::class.java.newInstance().also(builder).openFor(this)
    } catch (ex: Throwable) {
        ex.printStackTrace()
    }
}
