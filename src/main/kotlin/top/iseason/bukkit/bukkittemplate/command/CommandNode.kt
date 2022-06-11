package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import top.iseason.bukkit.bukkittemplate.TemplatePlugin
import top.iseason.bukkit.bukkittemplate.utils.sendColorMessage

@Suppress("unused")
open class CommandNode(
    /**
     * 节点名称
     */
    val name: String,
    /**
     * 节点别名
     */
    val alias: Array<String>? = null,

    val description: String? = null,
    /**
     * 默认权限
     */
    val default: PermissionDefault = PermissionDefault.TRUE,
    /**
     * 命令执行
     */
    var onExecute: (CommandNode.(sender: CommandSender) -> Boolean)? = null
) : CommandExecutor, TabExecutor {
    var permission: Permission =
        Permission(TemplatePlugin.getPlugin().name + "." + name, default)

    var params: Array<String> = arrayOf()
        private set

    var parent: CommandNode? = null
        private set(value) {
            field = value
            if (field != null) {
                permission = Permission(field!!.permission.name + "." + name, default)
                val parentPerms = field!!.permission.name + ".*"
                var parentPerm = Bukkit.getPluginManager().getPermission(parentPerms)
                if (parentPerm == null) parentPerm = Permission(parentPerms, PermissionDefault.OP)
                permission.addParent(parentPerm, true)
                Bukkit.getPluginManager().addPermission(permission)
            }
        }

    /**
     * 子节点
     */
    private val subNodes = mutableMapOf<String, CommandNode>()

    var successMessage: String? = null
    var failureMessage: String? = null
    var noPermissionMessage: String? = "&c你没有该命令的权限"

    /**
     * 添加子节点
     */
    fun addSubNode(node: CommandNode) {
        if (parent == null) Bukkit.getPluginManager().addPermission(permission)
        subNodes[node.name] = node
        node.parent = this
        node.alias?.forEach {
            subNodes[it] = node
        }
    }

    /**
     * @return null if not exists
     */
    fun getSubNode(arg: String) = subNodes[arg]

    /**
     * 获取该命令发送者可见的子节点
     * @return null if not exists
     */
    fun getSubNode(arg: String, sender: CommandSender): CommandNode? {
        val commandNode = getSubNode(arg) ?: return null
        if (!commandNode.canUse(sender)) return null
        return commandNode
    }

    /**
     * 获取根节点
     */
    fun getRootNode(): CommandNode = parent?.getRootNode() ?: this

    private fun canUse(sender: CommandSender): Boolean {
        return sender.hasPermission(permission)
    }

    fun getKeys(sender: CommandSender): MutableList<String> {
        val mutableListOf = mutableListOf<String>()
        subNodes.forEach { (k, v) ->
            if (!v.canUse(sender)) return@forEach
            mutableListOf.add(k)
        }
        return mutableListOf
    }

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
        val param = params.getOrNull(index) ?: throw ParmaException("Param is not exist")
        return TypeParam.getParam(param)
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): List<String>? {
        require(parent == null) { "只有根节点才能使用" }

        var node: CommandNode = this
        // 不完整参数
        var incomplete = ""
        var deep = 0
        for ((index, arg) in args.withIndex()) {
            if (arg.isBlank() && args.getOrNull(index + 1) != null) return emptyList()
            val subNode = node.getSubNode(arg, sender)
            if (subNode == null) {
                incomplete = arg
                deep = index
                break
            }
            node = subNode
        }
        return node.getKeys(sender).filter { it.startsWith(incomplete) }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        require(parent == null) { "只有根节点才能使用" }
        var node: CommandNode = this
        var deep = 0
        for (arg in args) {
            node = node.getSubNode(arg) ?: break
            deep++
        }
        with(node) {
            if (!canUse(sender)) {
                sender.sendColorMessage(noPermissionMessage)
                return true
            }
            params = args.copyOfRange(deep, args.size)
            if (onExecute == null) return true
            try {
                if (onExecute!!.invoke(this, sender)) {
                    sender.sendColorMessage(successMessage)
                } else sender.sendColorMessage(failureMessage)
            } catch (e: ParmaException) {
                //参数错误的提示
                if (e.typeParam != null) sender.sendColorMessage(e.typeParam.errorMessage(e.arg))
                else e.printStackTrace()
            }
        }
        return true
    }

}