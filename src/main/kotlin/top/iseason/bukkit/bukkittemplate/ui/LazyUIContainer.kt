package top.iseason.bukkit.bukkittemplate.ui

/**
 * 多页UI，采用懒加载模式，当UI显示时才初始化,UI应该具有空构造函数
 */
open class LazyUIContainer(
    private val pageTypes: List<Class<out Pageable>>
) : UIContainer(arrayOfNulls(pageTypes.size)) {

    override fun getCurrentPage(): BaseUI {
        if (pages[currentIndex] == null) {
            val pageable = pageTypes[currentIndex].newInstance() as Pageable
            pageable.getUI().build()
            pageable.container = this
            pages[currentIndex] = pageable
        }
        return super.getCurrentPage()!!
    }

}
