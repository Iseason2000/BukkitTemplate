package com.example.bukkit.templateplugin

import com.example.bukkit.templateplugin.ui.MultiUI
import com.example.bukkit.templateplugin.ui.MyUI
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import top.iseason.bukkittemplate.command.*
import top.iseason.bukkittemplate.ui.openPageableUI
import top.iseason.bukkittemplate.ui.openUI
import top.iseason.bukkittemplate.utils.bukkit.MessageUtils.sendColorMessage

fun command1() {
    command("playerutil") {
        description = "测试命令1"
        alias = arrayOf("playerutil2", "playerutil3")
        node("potion") {
            description = "玩家药水控制"
            default = PermissionDefault.OP
            isPlayerOnly = true
            param("<操作>", suggest = listOf("add", "set", "remove"))
            param("<药水类型>", suggest = ParamSuggestCache.potionTypes)
            param("[玩家]", suggestRuntime = ParamSuggestCache.playerParam)
            param("[等级]", suggest = listOf("0", "1", "2", "3", "4"))
            param("[秒]", suggest = listOf("1", "5", "10"))

            executor { params, sender ->
                val operation = params.next<String>()
                if (operation !in setOf("add", "set", "remove"))
                    throw ParmaException("&7参数 &c${operation}&7 不是一个有效的操作,支持的有: add、set、remove")
                val type = params.next<PotionEffectType>()
                val player = params.nextOrNull<Player>() ?: sender as Player
                val level = params.nextOrNull<Int>() ?: 0
                var time = ((params.nextOrNull<Double>() ?: 10.0) * 20.0).toInt()
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

                    "remove" -> {
                        player.removePotionEffect(type)
                    }
                }
            }
        }
    }
    command("testcommand") {
        description = "测试命令"
        alias = arrayOf("test1", "test2")
        node("other") {
            default = PermissionDefault.OP
            description = "测试节点2"
            node("test3") {
                alias = arrayOf("test1", "test2")
                description = "测试命令"
                onExecute = CommandNodeExecutor { params, sender ->
                    sender.sendColorMessage("#66ccff 这是一个测试命令!")
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
    node.params = listOf(
        Param("<玩家>", suggestRuntime = ParamSuggestCache.playerParam),
        Param("[数字]", listOf("1", "5", "10", "-5", "-1"))
    )
    node.onExecute = CommandNodeExecutor { params, sender ->
        val param1 = params.getParam<Int>(0)
        val param2 = params.getOptionalParam<Double>(1)
        sender.sendColorMessage(param1)
        sender.sendColorMessage(param2)
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
        }.onExecute = CommandNodeExecutor { params, sender ->
            (sender as Player).openUI<MyUI> {
                title = sender.displayName
            }
        }
        val node = node("MultiUI")
        node.isPlayerOnly = true
        node.onExecute = CommandNodeExecutor { params, sender ->
            (sender as Player).openPageableUI<MultiUI>()
        }
    }

}

/**
 * 结构命令
 */
fun testNode() = node("node3") {
    onExecute = CommandNodeExecutor { params, sender ->
        sender.sendMessage("hello")
    }
}

object TestNode : CommandNode(
    "testnode",
    alias = arrayOf("testnode2", "testnode3"),
    default = PermissionDefault.OP,
    async = true,
    description = "测试命令-单独类",
    params = listOf(
        Param("<玩家>", suggestRuntime = ParamSuggestCache.playerParam),
        Param("[金钱]", listOf("1", "5", "10", "-5", "-1"))
    )
) {
    init {
        onExecute = CommandNodeExecutor { params, sender ->
            val player = params.getParam<Player>(0)
            val money = params.getOptionalParam<Double>(1)
            player.sendColorMessage(money.toString())
        }
    }
}

