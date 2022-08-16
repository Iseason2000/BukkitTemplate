package com.example.bukkit.templateplugin


import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.bukkittemplate.ui.container.*
import top.iseason.bukkit.bukkittemplate.ui.slot.Button
import top.iseason.bukkit.bukkittemplate.ui.slot.onClicked
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage
import top.iseason.bukkit.bukkittemplate.utils.toColor


class MultiUI : LazyUIContainer(listOf(Page1::class.java, Page2::class.java, Page3::class.java)) {
    override var onPageChanged: ((from: Int, to: Int) -> Unit)? = { from, to ->
//        info("from $from to $to")
    }
}

class Page1 : ChestUI("page1" + System.currentTimeMillis()), Pageable {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第一页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        container?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        container?.nextPage(it.whoClicked)
    }.setup()
    override var container: UIContainer? = null
    override fun getUI(): BaseUI = this
}

class Page2 : ChestUI("page2" + System.currentTimeMillis()), Pageable {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第二页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        container?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        container?.nextPage(it.whoClicked)
    }.setup()
    override var container: UIContainer? = null
    override fun getUI(): BaseUI = this
}

class Page3 : ChestUI("page3" + System.currentTimeMillis()), Pageable {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第三页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        container?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        container?.nextPage(it.whoClicked)
    }.setup()
    override var container: UIContainer? = null
    override fun getUI(): BaseUI = this
}
