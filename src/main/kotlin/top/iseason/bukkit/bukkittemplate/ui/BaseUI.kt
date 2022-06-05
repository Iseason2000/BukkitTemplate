package top.iseason.bukkit.bukkittemplate.ui

import top.iseason.bukkit.bukkittemplate.common.submit
import top.iseason.bukkit.bukkittemplate.debug.warn
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack


abstract class BaseUI(
    val size: Int,
    /**
     * 点击间隔，防止卡顿
     */
    open var clickDelay: Long = 100L
) : InventoryHolder {
    abstract var baseInventory: Inventory
    final override fun getInventory(): Inventory = baseInventory

    /**
     * 储存各种槽位
     */
    var slots: Array<Slot?> = arrayOfNulls(size)

    /**
     * 当玩家打开界面时，分为上下2个Inventory，是否锁住上部分
     */
    var lockOnTop: Boolean = false

    /**
     * 当玩家打开界面时，分为上下2个Inventory，是否锁住下部分
     */
    var lockOnBottom: Boolean = false

    /**
     * 点击时触发，可以取消
     */
    open var onClick: InventoryClickEvent.() -> Unit = {}

    /**
     * 点击后触发，不可取消
     */
    open var onClicked: InventoryClickEvent.() -> Unit = {}

    /**
     * 关闭时触发
     */
    open var onClose: InventoryCloseEvent?.() -> Unit = {}

    /**
     * 打开时触发
     */
    open var onOpen: InventoryOpenEvent.() -> Unit = {}

    /**
     * 获取正在看这个UI的实体
     */
    fun getViewers() = baseInventory.viewers

    /**
     * 设置多个槽
     */
    fun addSlots(vararg slots: Slot) {
        for (slot in slots) {
            addSlot(slot)
        }
    }

    /**
     * 设置一个槽
     */
    fun addSlot(slot: Slot) {
        val index = slot.index
        try {
            //清除原有的ItemStack
            baseInventory.setItem(index, null)
            this.slots[index] = slot
            slot.baseInventory = baseInventory
            if (slot is IOSlot) {
                baseInventory.setItem(index, slot.placeholder)
            } else
                baseInventory.setItem(index, slot.itemStack)
        } catch (e: IndexOutOfBoundsException) {
            warn("Slot index was out of bounds(${index}), max index is ${inventory.size}")
        }
    }

    /**
     * 将一个Slot拷贝多份到其他格子,可变参数版
     */
    fun addMultiSlots(slot: Slot, vararg others: Int): List<Slot> {
        val mutableListOf = mutableListOf(slot)
        addSlot(slot)
        for (other in others) {
            val clone = slot.clone(other)
            addSlot(clone)
            mutableListOf.add(clone)
        }
        return mutableListOf
    }

    /**
     * 将一个Slot拷贝多份到其他格子
     */
    fun addMultiSlots(slot: Slot, others: Iterable<Int>): List<Slot> {
        val mutableListOf = mutableListOf(slot)
        addSlot(slot)
        for (other in others) {
            val clone = slot.clone(other)
            addSlot(clone)
            mutableListOf.add(clone)
        }
        return mutableListOf
    }

    /**
     * 将Slot 填满Inventory
     */
    fun setBackGround(slot: Slot): List<Slot> = addMultiSlots(slot, 0 until baseInventory.size)

    /**
     * 获取某一槽
     */
    fun getSlot(index: Int): Slot? = slots.getOrNull(index)

    /**
     * 用于重置界面到初始状态
     */
    abstract fun reset()

    /**
     * 重置所有槽位
     */
    fun resetSlots() {
        for (slot in slots) {
            slot?.reset()
        }
    }

    /**
     * 将可输出的物品发给玩家
     */
    fun ejectItems(player: HumanEntity) {
        //弹出未拿出的物品
        for (slot in slots) {
            if (slot !is IOSlot) continue
            slot.eject(player)
        }
    }

    /**
     * 向所有满足条件的可输入端口输入物品
     * @param itemStack 待填充的物品
     * @return 剩余的物品
     */
    fun inputItem(itemStack: ItemStack?): ItemStack? {
        if (lockOnTop) return itemStack
        var temp: ItemStack? = itemStack
        for (slot in slots) {
            if (slot == null || slot !is IOSlot) continue
            if (temp == null) break
            val invItem = baseInventory.getItem(slot.index)
            //不是空的 不是占位符且不相似物品一定不可入
            if (invItem != null && invItem != slot.placeholder && !temp.isSimilar(invItem)) continue
            if (!slot.input(slot, temp)) continue
            //可以直接放进
            if (invItem == null || invItem == slot.placeholder) {
                slot.itemStack = temp
                val tempInput = temp.clone()
                temp = null
                submit {
                    slot.onInput(slot, tempInput)
                }
                break
            }
            val temp2 = invItem.merge(temp)
            //不兼容原有的物品
            if (temp == temp2) continue
            val tempInput = if (temp2 == null) {
                //说明没有余量
                temp.clone()
            } else {
                //说明有余量
                temp.clone().apply { amount -= temp2.amount }
            }
            temp = temp2
            submit {
                slot.onInput(slot, tempInput)
            }
        }
        return temp
    }

    companion object {
        /**
         * 从 Inventory 获取 UI 对象
         */
        fun fromInventory(inventory: Inventory) = inventory.holder as? BaseUI
    }
}

/**
 * 设置点击时的动作
 */
fun <T : BaseUI> T.onClick(action: InventoryClickEvent.() -> Unit): T {
    this.onClick = action
    return this
}

/**
 * 设置点击后的动作
 */
fun <T : BaseUI> T.onClicked(action: InventoryClickEvent.() -> Unit): T {
    this.onClicked = action
    return this
}