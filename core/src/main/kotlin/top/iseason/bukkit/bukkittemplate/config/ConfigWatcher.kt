package top.iseason.bukkit.bukkittemplate.config

import org.bukkit.scheduler.BukkitRunnable
import top.iseason.bukkit.bukkittemplate.BukkitTemplate
import top.iseason.bukkit.bukkittemplate.DisableHook
import java.io.File
import java.lang.Thread.sleep
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService

class ConfigWatcher private constructor(private val folder: File) : BukkitRunnable() {
    private var isEnable = true
    private var service: WatchService = FileSystems.getDefault().newWatchService()
        .apply { folder.toPath().register(this, StandardWatchEventKinds.ENTRY_MODIFY) }

    override fun run() {
        FileSystems.getDefault().newWatchService()
        while (isEnable) {
            val key = try {
                service.take()
            } catch (e: Exception) {
                cancel()
                break
            }
            //监听文件修改
            event@ for (pollEvent in key.pollEvents()) {
                if (pollEvent.kind() != StandardWatchEventKinds.ENTRY_MODIFY) continue
                val path = pollEvent.context() as Path
                if (!path.toString().endsWith(".yml")) break@event
                val absolutePath = "${folder}${File.separatorChar}$path"
                val simpleYAMLConfig = SimpleYAMLConfig.configs[absolutePath] ?: continue
                if (simpleYAMLConfig.isAutoUpdate) {
                    sleep(200)
                    simpleYAMLConfig.load()
                    sleep(300)
                    service.close()
                    service = FileSystems.getDefault().newWatchService()
                        .apply { folder.toPath().register(this, StandardWatchEventKinds.ENTRY_MODIFY) }
                    break@event
                }
            }
            key.reset()
        }
    }

    override fun cancel() {
        super.cancel()
        isEnable = false
        service.close()
    }

    companion object {
        private val folders = mutableMapOf<String, ConfigWatcher>()

        init {
            DisableHook.addTask {
                onDisable()
            }
        }

        fun fromFile(file: File): ConfigWatcher {
            val parentFile = file.absoluteFile.parentFile
            val folder = parentFile.toString()
            val existWatcher = folders[folder]
            if (existWatcher != null) return existWatcher
            val configWatcher = ConfigWatcher(parentFile)
            folders[folder] = configWatcher
            configWatcher.runTaskAsynchronously(BukkitTemplate.getPlugin())
            return configWatcher
        }

        fun onDisable() {
            for (v in folders.values) {
                v.cancel()
            }
            folders.clear()
        }

    }
}