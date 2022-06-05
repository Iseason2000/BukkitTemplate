package top.iseason.bukkit.bukkittemplate.utils.bukkit


import org.bukkit.entity.EntityType
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack

/**
 * 给予玩家物品,如果背包放不下将会放置玩家脚下
 * @param itemStack 待输入的物品
 *
 */
fun HumanEntity.giveItems(vararg itemStack: ItemStack) {
    val addItems = inventory.addItem(*itemStack).values
    for (addItem in addItems) {
        if (addItem == null) continue
        (world.spawnEntity(location, EntityType.DROPPED_ITEM) as Item).itemStack = addItem
    }
}
