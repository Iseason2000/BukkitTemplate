package top.iseason.bukkit.templateplugin

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import top.iseason.bukkit.bukkittemplate.debug.info
import top.iseason.bukkit.bukkittemplate.ui.*
import top.iseason.bukkit.bukkittemplate.utils.bukkit.applyMeta
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage

class MyUI : ChestUI("${ChatColor.YELLOW}测试UI", row = 6, clickDelay = 200L) {

    init {
        setBackGround(Icon(ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE), 0))
    }

    val messageButton = Button(
        ItemStack(Material.ANVIL).applyMeta {
            setDisplayName("按钮示例")
        },
    ).onClicked {
        it.whoClicked.sendColorMessage("&a 你点击了按钮")
    }.setup()

    val inputSlot = IOSlot(4, placeholder = ItemStack(Material.GREEN_STAINED_GLASS_PANE))
        .inputFilter {
            it.type == Material.APPLE
        }.onInput {
            getViewers()[0].sendColorMessage("%a 你放入了苹果")
            info("输入了苹果")
        }.outputFilter {
            info("无法输出")
            false
        }.setup()
}
