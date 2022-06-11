package top.iseason.bukkit.templateplugin

import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.command.commandRoot

fun myCommands() {
    commandRoot("test", alias = arrayOf("test1", "test2"), default = PermissionDefault.OP) {
        node("test2") {
            node("test3", default = PermissionDefault.TRUE) {
                onSuccess("&a命令已执行!")
                onFailure("&c命令执行失败!")
                onNoPermissions("&c你没有该命令的权限")
                onExecute {
                    false
                }
            }
        }
    }

}