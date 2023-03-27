package top.iseason.bukkittemplate.hook

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import top.iseason.bukkittemplate.BukkitTemplate
import top.iseason.bukkittemplate.debug.debug
import top.iseason.bukkittemplate.utils.bukkit.EventUtils.listen
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object BungeeCordHook {

    private const val BUNGEE_CORD_CHANNEL = "BungeeCord"

    @Volatile
    var bungeeCordEnabled = false
        private set

    private val bcListener: PluginMessageListener =
        PluginMessageListener { channel: String, _: Player?, _: ByteArray? ->
            if (bungeeCordEnabled) return@PluginMessageListener
            if (channel != BUNGEE_CORD_CHANNEL) {
                return@PluginMessageListener
            }
            debug("BungeeCord mode was enabled!")
            bungeeCordEnabled = true
        }

    @JvmStatic
    fun onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(BukkitTemplate.getPlugin(), "BungeeCord")
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitTemplate.getPlugin(), "BungeeCord", bcListener)
        val player = Bukkit.getOnlinePlayers().firstOrNull()
        if (player != null) check(player)
        listen<PlayerLoginEvent> {
            check(this@listen.player)
        }
    }

    private fun check(player: Player) {
        val bs = ByteArrayOutputStream()
        val out = DataOutputStream(bs)
        out.writeUTF("IP")
        try {
            player.sendPluginMessage(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, bs.toByteArray())
        } catch (_: Exception) {
        }
    }

    @JvmStatic
    fun onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL)
        Bukkit.getMessenger().unregisterIncomingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL)
    }

    /**
     * 注册 BungeeCord 消息通道
     */
    fun register(listener: PluginMessageListener) {
        Bukkit.getMessenger().registerIncomingPluginChannel(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, listener)
    }

    fun broadcast(message: String) {
        broadcast(message, "Message")
    }

    private fun broadcast(message: String, type: String) {
        val bs = ByteArrayOutputStream()
        val out = DataOutputStream(bs)
        out.writeUTF(type)
        out.writeUTF("ALL")
        out.writeUTF(message)
        try {
            Bukkit.getOnlinePlayers()
                .randomOrNull()?.sendPluginMessage(BukkitTemplate.getPlugin(), BUNGEE_CORD_CHANNEL, bs.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            bungeeCordEnabled = false
            debug("BungeeCord mode was disabled!")
        }
    }

    fun broadcastRaw(message: String) {
        broadcast(message, "MessageRaw")
    }
}