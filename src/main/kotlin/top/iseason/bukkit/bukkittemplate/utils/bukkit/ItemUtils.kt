package top.iseason.bukkit.bukkittemplate.utils.bukkit

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
    if (i <= 0) type = Material.AIR
    else amount = i
}

fun Material.isAir(): Boolean = when (this.name) {
    "VOID_AIR",
    "CAVE_AIR",
    "AIR",
    "LEGACY_AIR" -> true
    else -> false
}