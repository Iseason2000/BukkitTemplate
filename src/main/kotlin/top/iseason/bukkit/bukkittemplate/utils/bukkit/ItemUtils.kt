package top.iseason.bukkit.bukkittemplate.core.utils.bukkit

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * 修改ItemMeta
 */
inline fun <T : ItemStack> T.applyMeta(block: ItemMeta.() -> Unit): T {
    val itemMeta = itemMeta ?: return this
    block(itemMeta)
    this.itemMeta = itemMeta
    return this
}

fun ItemStack.subtract(count: Int) {
    val i = amount - count
    if (i <= 0) type = Material.VOID_AIR
    else amount = i
}