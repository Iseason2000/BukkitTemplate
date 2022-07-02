package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.entity.Player

/**
 * 多页UI
 */
@Suppress("unused")
open class PageableUI(
    /**
     * 储存多页数组
     */
    protected val pages: Array<BaseUI?>
) {
    val size = pages.size

    // 页码
    protected var currentIndex = 0

    /**
     * 获取当前页码的UI
     */
    open fun getCurrentPage(): BaseUI? = pages[currentIndex]

    /**
     * 获取下一页，并将页码设置为下一页
     */
    open fun nextPage(): BaseUI? {
        currentIndex = (currentIndex + 1) % size
        return getCurrentPage()
    }

    /**
     * 获取上一页，并将页码设置为上一页
     */
    open fun lastPage(): BaseUI? {
        currentIndex = (currentIndex - 1) % size
        return getCurrentPage()
    }

    /**
     * 定位到第 page 页
     */
    open fun setPage(page: Int): BaseUI? {
        require(page in 0..size) { "page $page is not exist!" }
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
        player.openInventory(currentPage.baseInventory)
    }

}