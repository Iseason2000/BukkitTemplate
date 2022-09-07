package top.iseason.bukkit.bukkittemplate.command

import org.bukkit.Bukkit
import org.bukkit.command.PluginCommand
import org.bukkit.command.SimpleCommandMap
import org.bukkit.permissions.Permission
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.SimplePluginManager
import top.iseason.bukkit.bukkittemplate.BukkitTemplate
import top.iseason.bukkit.bukkittemplate.DisableHook
import top.iseason.bukkit.bukkittemplate.utils.MessageUtils.toColor
import java.lang.reflect.Constructor
import java.util.*
import java.util.stream.Collectors

@Suppress("unused")
object CommandHandler {

    private val pluginPermissions = mutableSetOf<Permission>()
    private val registeredCommands = mutableListOf<PluginCommand>()
    private val simpleCommandMap: SimpleCommandMap
    private val pluginCommandConstructor: Constructor<PluginCommand> = getPluginCommandConstructor()

    init {
        val simplePluginManager = Bukkit.getServer().pluginManager as SimplePluginManager
        val commandMapField = SimplePluginManager::class.java.getDeclaredField("commandMap")
        commandMapField.isAccessible = true
        simpleCommandMap = commandMapField.get(simplePluginManager) as SimpleCommandMap
        DisableHook.addTask { onDisable() }
    }

    private fun getPluginCommandConstructor(): Constructor<PluginCommand> {
        val constructor = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java)
        constructor.isAccessible = true
        return constructor
    }

    /**
     * 将命令节点注册为根命令
     */
    fun register(commandNode: CommandNode) {
        require(commandNode.parent == null) { "只能为根命令注册!" }
        val pluginCommand =
            pluginCommandConstructor.newInstance(commandNode.name, BukkitTemplate.getPlugin()) as PluginCommand
        if (commandNode.alias != null) {
            pluginCommand.aliases = Arrays.stream(commandNode.alias).collect(Collectors.toList())
        }
        if (commandNode.description != null) {
            pluginCommand.description = commandNode.description!!
        }
        pluginCommand.permissionMessage = commandNode.noPermissionMessage?.toColor()
        pluginCommand.setExecutor(commandNode)
        pluginCommand.tabCompleter = commandNode
        simpleCommandMap.register(BukkitTemplate.getPlugin().name, pluginCommand)
        pluginPermissions.add(commandNode.permission)
        registeredCommands.add(pluginCommand)
    }

    // 注销根命令
    fun unregister(commandNode: CommandNode) {
        require(commandNode.parent == null) { "非根命令不需要注销!" }
        Bukkit.getServer().getPluginCommand(commandNode.name)?.unregister(simpleCommandMap)
    }

    @JvmStatic
    fun unregisterAll() {
        for (registeredCommand in registeredCommands) {
            registeredCommand.unregister(simpleCommandMap)
        }
    }

    fun addPermissions(perm: Permission) {
        pluginPermissions.add(perm)
    }

    fun clearPermissions() {
        for (pluginPermission in pluginPermissions) {
            Bukkit.getPluginManager().removePermission(pluginPermission)
        }
    }

    @JvmStatic
    fun onDisable() {
        clearPermissions()
        unregisterAll()
    }

    /**
     * 更新注册的命令以支持tab
     */
    @JvmStatic
    fun updateCommands() {
        try {
            Bukkit.getServer().apply {
                javaClass.getDeclaredMethod("syncCommands").invoke(this)
            }
        } catch (_: Exception) {
        }
    }

}

/**
 * 创建命令根节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun command(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = node(name, modify)
    CommandHandler.register(commandNode)
    return commandNode
}

/**
 * 为节点添加子节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun CommandNode.node(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = top.iseason.bukkit.bukkittemplate.command.node(name, modify)
    this.addSubNode(commandNode)
    return commandNode
}

/**
 * 创建一个没有上下关系的节点
 * 使用CommandNode::addSubNode添加子节点或将自己添加到某个父节点中
 *
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun node(name: String, modify: (CommandNode.() -> Unit)? = null): CommandNode {
    val commandNode = CommandNode(name)
    modify?.let { it(commandNode) }
    return commandNode
}

/**
 * 为节点添加子节点
 * @param name 名字
 * @param modify 对节点的修改
 * @return 创建的节点
 */
fun CommandNode.node(node: CommandNode): CommandNode {
    this.addSubNode(node)
    return node
}
