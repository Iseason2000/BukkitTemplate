package top.iseason.bukkit.bukkittemplate.core.config

import top.iseason.bukkit.bukkittemplate.core.debug.debug
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import top.iseason.bukkit.bukkittemplate.core.common.submit
import top.iseason.bukkit.bukkittemplate.core.config.annotations.Comment
import top.iseason.bukkit.bukkittemplate.core.config.annotations.FilePath
import top.iseason.bukkit.bukkittemplate.core.config.annotations.Key
import top.iseason.bukkit.bukkittemplate.core.debug.info
import top.iseason.bukkit.bukkittemplate.plugin
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Thread.sleep
import java.nio.file.*
import java.util.*

abstract class SimpleYAMLConfig(val defaultPath: String? = null, var isAutoUpdate: Boolean = true) {

    /**
     * 更新时间
     */
    var updateTime = 0L

    /**
     * 配置文件路径
     */
    private val configPath = getPath().apply {
        if (!exists()) {
            parentFile.mkdirs()
            createNewFile()
        }
    }

    /**
     * 配置对象
     */
    var config = YamlConfiguration.loadConfiguration(configPath)

    private val keys = mutableListOf<ConfigKey>().also { list ->
        this::class.java.declaredFields.forEach {
            val keyAnnotation = it.getAnnotation(Key::class.java) ?: return@forEach
            val key = keyAnnotation.key.ifEmpty { it.name.replace('_', '.') }
            val comments = mutableListOf<String>()
            it.getAnnotationsByType(Comment::class.java).forEach { an ->
                //注释内容遍历
                an.value.forEach { value ->
                    comments.add(value)
                }
            }
            list.add(ConfigKey(key, it, if (comments.isEmpty()) null else comments))
        }
    }

    init {
        ConfigWatcher.fromFile(configPath.absoluteFile)
        configs[configPath.absolutePath] = this
        loadAsync()
    }

    fun setUpdate(enable: Boolean) {
        isAutoUpdate = enable
    }

    /**
     * 将文件路径转化为文件系统标准
     */
    private fun normalizeFileStr(file: String) = file.replace('\\', File.separatorChar).replace('/', File.separatorChar)

    private fun getPath(): File {
        val dataFolder = plugin.dataFolder
        if (defaultPath != null) return File(dataFolder, normalizeFileStr(defaultPath)).absoluteFile
        val annotation = this::class.java.getAnnotation(FilePath::class.java)
        require(annotation != null) { "path must not null" }
        return File(dataFolder, normalizeFileStr(annotation.path)).absoluteFile
    }

    /**
     * 异步保存配置
     */
    fun saveAsync(notify: Boolean = true) {
        submit(async = true) {
            save(notify)
        }
    }

    /**
     * 保存配置
     */
    fun save(notify: Boolean = true) {
        update(false)
        onSaved?.invoke(config)
        updateTime = System.currentTimeMillis()
        if (notify)
            info("Config $configPath was saved!")
    }

    /**
     * 从文件异步加载配置
     */
    fun loadAsync(notify: Boolean = true) {
        submit(async = true) {
            load(notify)
        }
    }

    /**
     * 从文件加载配置
     */
    fun load(notify: Boolean = true) {
        if (!update(isReadOnly = true)) {
            return
        }
        onLoaded?.invoke(config)
        updateTime = System.currentTimeMillis()
        if (notify)
            info("Config $configPath was reloaded!")
    }

    /**
     * 更新配置
     * @param isReadOnly 是否只读
     * @return 更新成功返回true
     */
    private fun update(isReadOnly: Boolean): Boolean {
        if (System.currentTimeMillis() - updateTime < 1000L) return false
        sleep(200L)
        val loadConfiguration = YamlConfiguration.loadConfiguration(configPath)
        val temp = YamlConfiguration()
        val commentMap = mutableMapOf<String, String>()
        keys.forEach { key ->
            //获取并设置注释
            val comments = key.comments
            if (comments != null) {
                for (str in comments) {
                    val keyS = key.key
                    val noPathKey = keyS.substring(keyS.lastIndexOf('.') + 1)
                    //注释识别标识
                    val random = "comment-${UUID.randomUUID()}"
                    //传入注释内容，待转换
                    commentMap["$noPathKey-$random"] = "# $str"
                    //将注释当作键值写入配置文件
                    temp.set("${key.key}-$random", "")
                }
            }
            if (isReadOnly) {
                val value = loadConfiguration.get(key.key)
                if (value != null) {
                    //获取修改的键值
                    try {
                        key.setValue(value)
                    } catch (e: Exception) {
                        debug("Loading config $configPath error! key:${key.key} value: $value")
                    }
                }
            }
            //将数据写入临时配置
            try {
                temp.set(key.key, key.getValue())
            } catch (e: Exception) {
                debug("setting config $configPath error! key:${key.key}")
            }
        }
        //保存临时配置，此时注释尚未转换
        temp.save(configPath)
        //转换注释
        commentFile(configPath, commentMap)
        config = loadConfiguration
        return true
    }

    open val onLoaded: (FileConfiguration.() -> Unit)? = null
    open val onSaved: (FileConfiguration.() -> Unit)? = null

    /**
     * 转换配置文件的注释
     */
    private fun commentFile(file: File, commentMap: Map<String, String>) {
        // 创建临时文件
        val commentedFile = File(file.path + ".tmp")
        val newFile: MutableList<String> = ArrayList()
        //逐行扫描,匹配注释并替换
        Scanner(file, "UTF-8").use { scanner ->
            while (scanner.hasNextLine()) {
                var nextLine: String = scanner.nextLine()
                for ((key, value) in commentMap) {
                    if (nextLine.contains(key)) {
                        if (value == "# ") {
                            nextLine = ""
                            break
                        }
                        nextLine = nextLine.substring(0, nextLine.indexOf(key)) + value
                        break
                    }
                }
                newFile.add(nextLine)
            }
            //写入数据到临时文件
            Files.write(commentedFile.toPath(), newFile)
            //复制替换
            copyFileUsingStream(commentedFile, file)
            //删除临时文件
            Files.delete(commentedFile.toPath())
        }

    }

    /**
     * 复制文件内容
     */
    @Throws(IOException::class)
    private fun copyFileUsingStream(source: File, dest: File) {
        FileInputStream(source).use { fis ->
            FileOutputStream(dest).use { fos ->
                val buffer = ByteArray(1024)
                var length: Int
                while (fis.read(buffer).also { length = it } > 0) {
                    fos.write(buffer, 0, length)
                }
            }
        }
    }

    companion object {
        //监听器列表
        val configs = mutableMapOf<String, SimpleYAMLConfig>()
    }
}
