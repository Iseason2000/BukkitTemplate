package com.example.bukkit.templateplugin

import com.example.bukkit.templateplugin.ui.MultiUI
import com.example.bukkit.templateplugin.ui.MyUI
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkit.bukkittemplate.command.*
import top.iseason.bukkit.bukkittemplate.ui.openPageableUI
import top.iseason.bukkit.bukkittemplate.ui.openUI
import top.iseason.bukkit.bukkittemplate.utils.MessageUtils.sendColorMessage

fun command1() {
    command("playerutil") {
        description = "测试命令1"
        alias = arrayOf("test1", "test2")
        node("potion") {
            description = "玩家药水控制"
            default = PermissionDefault.OP
            isPlayerOnly = true
            params = arrayOf(
                Param("<操作>", listOf("add", "set", "remove")),
                Param("<药水类型>", ParamSuggestCache.potionTypes),
                Param("[玩家]", suggestRuntime = ParamSuggestCache.playerParam),
                Param("[等级]", listOf("0", "1", "2", "3", "4")),
                Param("[秒]", listOf("1", "5", "10"))
            )
            onExecute = {
                val operation = getParam<String>(0)
                if (operation !in setOf("add", "set", "remove"))
                    throw ParmaException("&7参数 &c${operation}&7 不是一个有效的操作,支持的有: add、set、remove")
                val type = getParam<PotionEffectType>(1)
                var player = getOptionalParam<Player>(2)
                var reduce = 0
                if (player == null) {
                    player = it as Player
                    reduce++
                }
                var level = getOptionalParam<Int>(3 - reduce)
                if (level == null) {
                    level = 0
                    reduce++
                }
                var time = ((getOptionalParam<Double>(4 - reduce) ?: 10.0) * 20.0).toInt()

                when (operation) {
                    "add" -> {
                        val potionEffect = player.getPotionEffect(type)
                        if (potionEffect != null) time += potionEffect.duration
                        player.addPotionEffect(PotionEffect(type, time, level))
                    }

                    "set" -> {
                        player.removePotionEffect(type)
                        player.addPotionEffect(PotionEffect(type, time, level))
                    }

                    else -> {
                        player.removePotionEffect(type)
                    }
                }
            }
        }
    }
    command("testcommand") {
        description = "测试命令1"
        alias = arrayOf("test1", "test2")
        node("other") {
            default = PermissionDefault.OP
            description = "测试节点2"
            node("test3") {
                alias = arrayOf("test1", "test2")
                description = "测试命令"
                onExecute = {
                    it.sendColorMessage("#66ccff 这是一个测试命令")
                }
            }
        }
    }
}

fun command2() {
    val node = command(
        "2node",
    )
    node.alias = arrayOf("node2", "node3")
    node.default = PermissionDefault.OP
    node.async = true
    node.description = "测试命令2"
    node.params = arrayOf(
        Param("<玩家>", suggestRuntime = ParamSuggestCache.playerParam),
        Param("[数字]", listOf("1", "5", "10", "-5", "-1"))
    )
    node.onExecute = {
        val param1 = getParam<Int>(0)
        val param2 = getOptionalParam<Double>(1)
        it.sendColorMessage(param1)
        it.sendColorMessage(param2)
    }

}

fun command3() {
    TestNode.registerAsRoot()
}

fun command4() {
    command("testcommand") {
        node("node1") {
            node("node2") {
                node(testNode())
            }
        }
        node("node4")
        node("node5")
    }
}

fun openUICommand() {
    command("openUI") {
        isPlayerOnly = true
        async = true
        node("UI") {
            isPlayerOnly = true
        }.onExecute = {
            (it as Player).openUI<MyUI> {
                title = it.displayName
            }
        }
        val node = node("MultiUI")
        node.isPlayerOnly = true
        node.onExecute = {
            (it as Player).openPageableUI<MultiUI>()
        }
    }

}

/**
 * 结构命令
 */
fun testNode() = node("node3") {
    onExecute = {
        it.sendMessage("hello")
    }
}

object TestNode : CommandNode(
    "testnode",
    alias = arrayOf("testnode2", "testnode3"),
    default = PermissionDefault.OP,
    async = true,
    description = "测试命令-单独类",
    params = arrayOf(
        Param("<玩家>", suggestRuntime = ParamSuggestCache.playerParam),
        Param("[金钱]", listOf("1", "5", "10", "-5", "-1"))
    )
) {
    init {
        onExecute = {
            val player = getParam<Player>(0)
            val money = getOptionalParam<Double>(1)
            player.sendMessage(money.toString())
        }
    }
}

