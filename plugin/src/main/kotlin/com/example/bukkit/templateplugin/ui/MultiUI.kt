package com.example.bukkit.templateplugin.ui


import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import top.iseason.bukkittemplate.ui.container.BaseUI
import top.iseason.bukkittemplate.ui.container.ChestUI
import top.iseason.bukkittemplate.ui.container.LazyUIContainer
import top.iseason.bukkittemplate.ui.container.UIContainer
import top.iseason.bukkittemplate.ui.slot.Button
import top.iseason.bukkittemplate.ui.slot.getContainer
import top.iseason.bukkittemplate.ui.slot.onClicked
import top.iseason.bukkittemplate.utils.bukkit.ItemUtils.applyMeta
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.toColor


class MultiUI : UIContainer(arrayOf(Page1(), Page2(), Page3())) {
    override var onPageChanged: ((from: Int, to: Int) -> Unit)? = { from, to ->
//        info("from $from to $to")
    }
}

class LazyMultiUI : LazyUIContainer(arrayOf(Page1::class.java, Page2::class.java, Page3::class.java)) {
    //重写以支持非空构造方法的类
    override fun onInit(clazz: Class<out BaseUI>): BaseUI {
        return clazz.newInstance()
    }

    override var onPageChanged: ((from: Int, to: Int) -> Unit)? = { from, to ->
//        info("from $from to $to")
    }
}

class Page1 : ChestUI("page1" + System.currentTimeMillis()) {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第一页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        getContainer()?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        getContainer()?.nextPage(it.whoClicked)
    }.setup()
}

class Page2 : ChestUI("page2" + System.currentTimeMillis()) {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第二页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        getContainer()?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        getContainer()?.nextPage(it.whoClicked)
    }.setup()
}

class Page3 : ChestUI("page3" + System.currentTimeMillis()) {
    val button = Button(ItemStack(Material.GREEN_WOOL), 4).onClicked {
        it.whoClicked.sendColorMessage("&a当前是第三页")
    }.setup()
    val last = Button(ItemStack(Material.PUMPKIN).applyMeta { setDisplayName("&c上一页".toColor()) }, 0).onClicked {
        getContainer()?.lastPage(it.whoClicked)
    }.setup()
    val next = Button(ItemStack(Material.MELON).applyMeta { setDisplayName("&a下一页".toColor()) }, 8).onClicked {
        getContainer()?.nextPage(it.whoClicked)
    }.setup()
}
