package top.iseason.bukkit.bukkittemplate.persistence.config

import java.lang.reflect.Field

class ConfigKey(val key: String, val field: Field, val comments: List<String>?) {

    fun setValue(value: Any) {
        field.isAccessible = true
        field.set(field, value)
        field.isAccessible = false
    }

    fun getValue(): Any? {
        field.isAccessible = true
        val get = field.get(field)
        field.isAccessible = false
        return get
    }
}