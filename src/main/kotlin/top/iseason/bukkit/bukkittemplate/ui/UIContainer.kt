package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player

/**
 * 多页UI
 */
@Suppress("unused")
open class UIContainer(
    /**
     * 储存多页数组
     */
    protected val pages: Array<Pageable?>
) {
    val size = pages.size

    // 页码
    protected var currentIndex = 0

    /**
     * 翻页时调用
     * @param from 源页码
     * @param to 目标页码
     */
    open var onPageChanged: ((from: Int, to: Int) -> Unit)? = null

    /**
     * 获取当前页码的UI
     */
    open fun getCurrentPage(): BaseUI? {
        val pageable = pages[currentIndex] ?: return null
        pageable.container = this
        return pageable.getUI()
    }

    /**
     * 获取下一页，并将页码设置为下一页
     */
    open fun nextPage(): BaseUI? {
        return setPage((currentIndex + 1) % size)
    }

    open fun nextPage(player: HumanEntity) {
        player.openInventory(nextPage()?.inventory ?: return)
    }

    /**
     * 获取上一页，并将页码设置为上一页
     */
    open fun lastPage(): BaseUI? {
        var last = currentIndex - 1
        if (last < 0) last += size
        return setPage(last)
    }

    open fun lastPage(player: HumanEntity) {
        player.openInventory(lastPage()?.inventory ?: return)
    }

    /**
     * 定位到第 page 页
     */
    open fun setPage(page: Int): BaseUI? {
        require(page in 0..size) { "page $page is not exist!" }
        onPageChanged?.invoke(currentIndex, page)
        currentIndex = page
        return getCurrentPage()
    }

    /**
     * 为某个玩家打开UI
     */
    fun openFor(player: Player) {
        require(pages.isNotEmpty()) { "Your pageable ui must possess at lease 1 page" }
        val currentPage = getCurrentPage()
        require(currentPage != null) { "index $currentIndex is not exist!" }
        player.openInventory(currentPage.inventory)
    }

}