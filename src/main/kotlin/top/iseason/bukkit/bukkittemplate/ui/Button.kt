package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * 可点击的按钮
 * @param rawMaterial 按钮默认材质
 * @param rawDisplayName 默认显示名
 * @param rawLore 默认显示lore
 * @param index 位于容器中的位置
 */

open class Button(
    override val rawItemStack: ItemStack,
    index: Int = 0,
) : ClickSlot(rawItemStack, index) {
    var itemMeta: ItemMeta
        get() =
            itemStack!!.itemMeta!!
        set(value) {
            itemStack!!.itemMeta = value
        }

    /**
     * 显示的图标
     */
    var material: Material
        get() =
            itemStack!!.type
        set(value) {
            itemStack!!.type = value
        }

    /**
     * 显示的名称
     */
    var displayName: String
        get() = itemMeta.displayName
        set(value) {
            itemMeta = itemMeta.apply { setDisplayName(value) }
        }

    /**
     * 显示的lore
     */
    var lore: List<String>
        get() = itemMeta.lore ?: emptyList()
        set(value) {
            itemMeta = itemMeta.apply { lore = value }
        }

    override fun reset() {
        itemStack = rawItemStack
    }

    override fun clone(index: Int): Button = Button(rawItemStack, index).also {
        it.itemStack = itemStack
        it.itemMeta = itemMeta
        it.baseInventory = baseInventory
        it.material = material
        it.displayName = displayName
        it.lore = lore
        it.onClick = onClick
        it.onClicked = onClicked
    }

}