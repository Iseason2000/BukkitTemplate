package top.iseason.bukkit.bukkittemplate

import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import top.iseason.bukkit.bukkittemplate.config.ConfigWatcher
import top.iseason.bukkit.bukkittemplate.config.SimpleYAMLConfig
import top.iseason.bukkit.bukkittemplate.ui.UIListener
import java.io.File
import java.net.JarURLConnection
import java.net.URISyntaxException
import java.net.URL
import java.util.jar.JarFile

lateinit var plugin: TemplatePlugin

class TemplatePlugin : JavaPlugin() {

    val classes = this::class.java.protectionDomain.codeSource.location.getClasses()
    val ktPlugin: KotlinPlugin = findInstance().also {
        it.javaPlugin = this
    }

    override fun onLoad() {
        plugin = this
        ktPlugin.onLoad()
    }

    override fun onEnable() {
        UIListener
        callConfigsInstance()
        ktPlugin.onEnable()
        if (ktPlugin.bstatsID > 0) {
            Metrics(this, ktPlugin.bstatsID)
        }

    }

    override fun onDisable() {
        UIListener.onDisable()
        ConfigWatcher.stop()
        ktPlugin.onDisable()
    }

    companion object {
        @JvmName("getPluginInstance")
        fun getInstance() = plugin
    }

    private fun findInstance(): KotlinPlugin {
        val pluginCLass = classes.firstOrNull {
            KotlinPlugin::class.java.isAssignableFrom(it)
        } ?: throw IllegalStateException("cannot find instance")
        val declaredField = pluginCLass.getDeclaredField("INSTANCE")
        declaredField.isAccessible = true
        return declaredField.get(null) as KotlinPlugin
    }

    private fun callConfigsInstance() {
        for (clazz in classes) {
            if (SimpleYAMLConfig::class.java.isAssignableFrom(clazz)) {
                try {
                    val declaredField = clazz.getDeclaredField("INSTANCE")
                    declaredField.isAccessible = true
                    declaredField.get(null)
                    declaredField.isAccessible = false
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun URL.getClasses(): List<Class<*>> {
        val classes = ArrayList<Class<*>>()
        val srcFile = try {
            File(toURI())
        } catch (ex: IllegalArgumentException) {
            File((openConnection() as JarURLConnection).jarFileURL.toURI())
        } catch (ex: URISyntaxException) {
            File(path)
        }
        JarFile(srcFile).stream().filter {
            it.name.endsWith(".class") && !it.name.startsWith("META-INF")
        }.forEach {
            val forName = try {
                Class.forName(
                    it.name.replace('/', '.').substring(0, it.name.length - 6),
                    false,
                    TemplatePlugin::class.java.classLoader
                )
            } catch (e: Exception) {
                return@forEach
            }
            //唤醒配置单例
            if (SimpleYAMLConfig::class.java.isAssignableFrom(forName)) {
                classes.add(forName)
            }
            if (!KotlinPlugin::class.java.isAssignableFrom(forName) || forName.name == KotlinPlugin::class.java.name) {
                return@forEach
            }
            classes.add(forName)
        }
        return classes
    }
}