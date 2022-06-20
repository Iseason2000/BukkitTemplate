package top.iseason.bukkit.bukkittemplate.utils

import java.util.*

class CoolDown<T> {
    private val coolDownMap: HashMap<T, Long> = HashMap()

    /**
     * 检查键值是否在冷却中
     * @param key 键值
     * @param coolDown 冷却时间
     */
    fun check(key: T, coolDown: Long): Boolean {
        return EasyCoolDown.checkType(coolDownMap, key, coolDown)
    }
}

class WeakCoolDown<T> {
    private val coolDownMap: WeakHashMap<T, Long> = WeakHashMap()

    /**
     * 检查键值是否在冷却中
     * @param key 键值
     * @param coolDown 冷却时间
     */
    fun check(key: T, coolDown: Long): Boolean {
        return EasyCoolDown.checkType(coolDownMap, key, coolDown)
    }
}

object EasyCoolDown {
    private val coolDownMap: WeakHashMap<String, Long> = WeakHashMap()

    /**
     * 检查键值是否在冷却中
     * @param key 键值
     * @param coolDown 冷却时间
     */
    fun check(obj: Any, coolDown: Long): Boolean {
        val key = obj.toString()
        return checkType(coolDownMap, key, coolDown)
    }

    // 检查某个map中某个键值是否在某个冷却时间内
    fun <T> checkType(map: MutableMap<T, Long>, obj: T, coolDown: Long): Boolean {
        val lastTime = map[obj]
        val current = System.currentTimeMillis()
        if (lastTime == null) {
            map[obj] = current
            return true
        }
        if (current - lastTime <= coolDown) return false
        map[obj] = current
        return true
    }
}