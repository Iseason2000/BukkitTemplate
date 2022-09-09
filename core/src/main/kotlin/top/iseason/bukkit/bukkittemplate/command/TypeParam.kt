package top.iseason.bukkit.bukkittemplate.command

import com.google.common.base.Enums
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType
import java.util.*

@Suppress("unused")
open class TypeParam<T : Any>(
    val type: Class<T>,
    var errorMessage: (String) -> String = { "Param ${it}is not exist" },
    val onCast: (String) -> T?
) {
    init {
        addTypeParam(this)
    }

    companion object {
        val typeParams = mutableMapOf<Class<*>, TypeParam<*>>()

        init {
            setDefaultParams()
        }

        fun addTypeParam(typeParam: TypeParam<*>) {
            typeParams[typeParam.type] = typeParam
        }

        fun getTypeParam(clazz: Class<*>) = typeParams[clazz]

        fun removeType(type: Class<*>) = typeParams.remove(type)

        // 可选参数
        fun <T> getOptionalTypedParam(clazz: Class<*>, paramStr: String): T? {
            val typeParam = typeParams[clazz]
            //匹配所有枚举
            if (typeParam == null && clazz.isEnum) {
                return Enums.getIfPresent(clazz as Class<out Enum<*>>, paramStr.uppercase()).orNull() as? T
            }
            if (typeParam == null) throw ParmaException("Param Type is not exist!")
            return typeParam.onCast(paramStr) as? T
        }

        //必须的参数
        fun <T> getTypedParam(clazz: Class<*>, paramStr: String): T {
            return getOptionalTypedParam(clazz, paramStr) ?: throw ParmaException(paramStr, typeParams[clazz])
        }
    }
}

/**
 * 默认提供的参数
 */
private fun setDefaultParams() {
    TypeParam(Player::class.java, errorMessage = { "&7玩家 &c${it} &7不存在!" }) {
        if (it.length == 36) {
            runCatching { Bukkit.getPlayer(UUID.fromString(it)) }.getOrNull()
        } else Bukkit.getPlayerExact(it)
    }
    TypeParam(OfflinePlayer::class.java, errorMessage = { "&7玩家 &c${it} &7不存在!" }) {
        var player: OfflinePlayer? = Bukkit.getOfflinePlayer(it)
        if (!player!!.hasPlayedBefore()) {
            player = runCatching {
                val offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(it))
                if (offlinePlayer.hasPlayedBefore()) offlinePlayer else null
            }.getOrNull()
        }
        player
    }
    TypeParam(Int::class.java, errorMessage = { "&c${it} &7不是一个有效的整数" }) {
        runCatching { it.toInt() }.getOrNull()
    }
    TypeParam(Double::class.java, errorMessage = { "&c${it} &7不是一个有效的小数" }) {
        runCatching { it.toDouble() }.getOrNull()
    }
    TypeParam(String::class.java) { it }
    TypeParam(
        PotionEffectType::class.java,
        { "&c${it} &7不是一个有效的药水种类" }
    ) {
        PotionEffectType.getByName(it)
    }

}