package top.iseason.bukkit.bukkittemplate.core.ui

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import top.iseason.bukkit.bukkittemplate.core.ui.BaseUI

/**
 * 箱子界面
 * @param title 标题
 * @param row 行数
 */
open class ChestUI(
    title: String = "Chest UI",
    row: Int = 6,
    override var clickDelay: Long = 200L
) : BaseUI(row * 9) {
    override var baseInventory: Inventory = Bukkit.createInventory(this, row * 9, title)

    override fun reset() {
        resetSlots()
    }

}