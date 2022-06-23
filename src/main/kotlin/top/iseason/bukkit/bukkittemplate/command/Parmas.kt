package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.debug.SimpleLogger

class Params(val params: Array<String>, val node: CommandNode) {
    /**
     * 获取指定参数类型的参数
     * @param index 参数的位置
     */
    inline fun <reified T> getOptionalParam(index: Int): T? {
        val param = params.getOrNull(index) ?: return null
        return TypeParam.getOptionalParam<T>(param)
    }

    /**
     * 获取指定参数类型的参数
     * @param index 参数的位置
     */
    inline fun <reified T> getParam(index: Int): T {
        val param = params.getOrNull(index) ?: throw ParmaException(
            "${SimpleLogger.prefix}&c参数 &6${
                node.params.getOrNull(index)?.placeholder ?: "位置 $index"
            } &c不存在!"
        )
        return TypeParam.getParam(param)
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
     * 建议
     */
    val suggest: (CommandSender.() -> Collection<String>)? = null
)

object ParamSuggestCache {
    val playerParam: CommandSender.() -> Collection<String> = { Bukkit.getOnlinePlayers().map { it.name } }
    private val potionTypes = PotionEffectType.values().filterNotNull().map {
        it.name.lowercase()
    }
    val potionEffects: CommandSender.() -> Collection<String> = { potionTypes }
}

