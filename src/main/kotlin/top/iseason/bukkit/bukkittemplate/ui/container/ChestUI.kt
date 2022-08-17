package top.iseason.bukkit.bukkittemplate.ui.container

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.inventory.Inventory

/**
 * 箱子界面
 * @param title 标题
 * @param row 行数
 */
open class ChestUI(
    var title: String = "Chest UI",
    row: Int = 6,
    override var clickDelay: Long = 200L
) : BaseUI(row * 9) {

    override fun reset() {
        resetSlots()
    }

    override fun serialize(section: ConfigurationSection) {
        section["title"] = title
        super.serialize(section)
    }

    override fun buildInventory(): Inventory = Bukkit.createInventory(this, super.size, title)


    override fun deserialize(section: ConfigurationSection): ChestUI? {
        if (!section.contains("row", true)) {
            return null
        }
        val chestUI = ChestUI(section.getString("title") ?: "", section.getInt("row"))
        with(chestUI) {
            clickDelay = section.getLong("clickDelay", 200L)
            lockOnTop = section.getBoolean("lockOnTop")
            lockOnBottom = section.getBoolean("lockOnBottom")
            val slotSection = section.getConfigurationSection("slots") ?: return@with
            for (serializeId in slotSection.getKeys(false)) {
                val find = this@ChestUI.slots.find {
                    serializeId == it?.serializeId
                } ?: continue
                val slot = slotSection.getConfigurationSection(serializeId) ?: continue
                val string = slot.getString("slot") ?: continue
                val ints = string.split(',').mapNotNull {
                    try {
                        it.toInt()
                    } catch (e: Throwable) {
                        null
                    }
                }
                inner@ for (int in ints) {
                    slot["slot"] = int
                    chestUI.addSlots(find.deserialize(slot) ?: continue@inner)
                }
            }
        }
        chestUI.onClick = onClick
        chestUI.onClicked = onClicked
        chestUI.onClose = onClose
        chestUI.onOpen = onOpen
        return chestUI
    }

    override fun clone(): BaseUI {
        val chestUI = ChestUI(this.title, this.size / 9, this.clickDelay)
        slots.forEachIndexed { index, baseSlot ->
            chestUI.slots[index] = baseSlot?.clone(index)
        }
        return chestUI.also {
            it.lockOnBottom = lockOnBottom
            it.lockOnTop = lockOnTop
            it.serializeId = serializeId
            it.async = async
            it.onClick = onClick
            it.onClicked = onClicked
            it.onOpen = onOpen
            it.onClose = onClose
        }
    }

    override var container: UIContainer? = null

    override fun getUI(): BaseUI = this

    override var serializeId: String = "chestui"
}