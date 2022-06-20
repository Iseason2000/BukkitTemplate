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

/**
 * 减少物品数量，如果小于0则物品变为空气
 */
fun ItemStack.subtract(count: Int) {
    val i = amount - count
    if (i <= 0) type = Material.AIR
    else amount = i
}

/**
 * 增加物品数量，返回溢出的数量
 */
fun ItemStack.add(count: Int): Int {
    val i = amount + count
    return if (i >= maxStackSize) {
        amount = maxStackSize
        i - maxStackSize
    } else {
        amount = i
        0
    }
}

/**
 * 检查材质是否是空气
 */
fun Material.checkAir(): Boolean = when (this.name) {
    "VOID_AIR",
    "CAVE_AIR",
    "AIR",
    "LEGACY_AIR" -> true
    else -> false
}