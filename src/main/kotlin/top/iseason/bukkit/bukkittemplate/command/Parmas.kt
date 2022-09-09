package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffectType

class Params(val params: Array<String>, val node: CommandNode) {
    /**
     * 获取指定参数类型的参数,不存在返回null
     * @param index 参数的位置
     */
    inline fun <reified T> getOptionalParam(index: Int): T? {
        val param = params.getOrNull(index) ?: return null
        return TypeParam.getOptionalTypedParam<T>(T::class.java, param)
    }

    /**
     * 获取指定参数类型的参数
     * @param index 参数的位置
     */
    inline fun <reified T> getParam(index: Int): T {
        val param = params.getOrNull(index)
            ?: throw ParmaException("&c参数 &6${node.params.getOrNull(index)?.placeholder ?: "位置 $index"} &c不存在!")
        return TypeParam.getTypedParam(T::class.java, param)
    }
}

/**
 * 命令参数
 */
open class Param(
    /**
     * 占位符
     */
    val placeholder: String,
    /**
     * 建议，存在运行时建议时不会使用
     */
    var suggest: Collection<String>? = null,
    /**
     * 运行时建议
     */
    var suggestRuntime: (CommandSender.() -> Collection<String>)? = null
)

/**
 * 参数建议缓存，避免无所谓的内存消耗
 */
object ParamSuggestCache {
    val playerParam: CommandSender.() -> Collection<String> = { Bukkit.getOnlinePlayers().map { it.name } }
    val potionTypes = PotionEffectType.values().filterNotNull().map {
        it.name.lowercase()
    }
    val materialTypes = Material.values().map {
        it.name.lowercase()
    }
}

