package top.iseason.bukkit.bukkittemplate.ui

/**
 * 多页UI，采用懒加载模式，当UI显示时才初始化,UI应该具有空构造函数
 */
open class LazyPageableUI(
    private val pageTypes: List<Class<out BaseUI>>
) : PageableUI(arrayOfNulls(pageTypes.size)) {

    override fun getCurrentPage(): BaseUI {
        if (pages[currentIndex] == null) {
            pages[currentIndex] = pageTypes[currentIndex].newInstance() as BaseUI
        }
        return super.getCurrentPage()!!
    }
}
