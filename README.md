# BukkitTemplate 使用文档

> 本项目是一个基于Kotlin+Bukkit的中小型插件开发模板，是我学习过程中的经验累积
>
> 这并不是一个多好的项目，远不能与现存的一些大型项目比较，如有错误与建议请不吝赐教

## 技术栈

* Bukkit API
* Java\Kotlin 编程语言
* Gradle Kotlin DSL 构建工具
* ShadowJar 包管理
* ProGuard 代码混淆与压缩
* Exposed+HikariCP 数据库框架(可选)
* Dokka JavaDoc文档生成(可选)

## 项目特点

* bukkit api 全版本支持(仅限core)
* 动态加载外部依赖
* 仅打包使用的代码(缩小体积)
* 支持混淆代码(开关)
* 提供基础库(命令、配置、数据、界面、消息等以及常用工具封装)
* 充分利用kotlin语言特性设计的API

## 链接

项目地址: https://github.com/Iseason2000/BukkitTemplate

JavaDoc: https://www.iseason.top/docs/BukkitTemplate/

WIKI: https://github.com/Iseason2000/BukkitTemplate/wiki

## 代码速览

### 命令

这是一条药水效果操作的命令 /playerutil potion <操作> <药水类型> [玩家] [等级] [秒]

~~~ kotlin
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

            executor {
                val operation = next<String>()
                if (operation !in setOf("add", "set", "remove"))
                    throw ParmaException("&7参数 &c${operation}&7 不是一个有效的操作,支持的有: add、set、remove")
                val type = next<PotionEffectType>()
                val player = nextOrNull<Player>() ?: it as Player
                val level = nextOrNull<Int>() ?: 0
                var time = ((nextOrNull<Double>() ?: 10.0) * 20.0).toInt()
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
~~~

### 配置

代码

~~~kotlin
@FilePath("config.yml")
object Config : SimpleYAMLConfig() {

   @Key("test.test1.test2")
   @Comment("", "6666")
   var test = 0

   @Key
   @Comment("", "list 测试")
   var test3 = mutableListOf("12312", "asdfas46", "tew4q5t456wefg6s")

   @Key
   @Comment("", "set 测试")
   var set = mutableSetOf("asdasd", "asdasdas", "asdas")

   @Comment("", "map 测试")
   @Key
   var map = mutableMapOf("test" to mutableMapOf("1" to mutableMapOf('1' to 2), "2" to "2"), "test2" to 2)

   override fun onLoaded(section: ConfigurationSection) {
      println("loaded")
   }

   override fun onSaved(section: ConfigurationSection) {
      println("saved")
   }
}
~~~

生成的文件

~~~ yaml
test:
  test1:

    # 6666
    test2: 0

# list 测试
test3:
- '12312'
- asdfas46
- tew4q5t456wefg6s

# set 测试
set:
- asdasd
- asdasdas
- asdas

# map 测试
map:
  test:
    '1':
      '1': 2
    '2': '2'
  test2: 2

~~~

如果你想要将所有成员都设置为key可以这样,适用于语言文件这类

~~~ kotlin
@Key
@FilePath("lang.yml")
object Lang : SimpleYAMLConfig(updateNotify = false) {
    var hello_message = "你好 世界"
    var welcome_message = "欢迎来到我的世界"
    var quit_message = "玩家 %player% 已退出了服务器"

    override fun onLoaded(section: ConfigurationSection) {
        info("语言文件已重载")
    }
}
~~~

**配置默认自动重载且保留注释, 请注意 key 必须为 var 声明**

### UI

```kotlin
class MyUI : ChestUI("${ChatColor.YELLOW}测试UI", row = 6, clickDelay = 500L), Pageable {

   init {
      setBackGround(Icon(ItemStack(Material.STONE), 0))
   }

   val messageButton = Button(
      ItemStack(Material.ANVIL).applyMeta {
         setDisplayName("${ChatColor.GREEN}按钮示例")
      },
   ).onClicked {
      it.whoClicked.sendColorMessage("&a 你点击了按钮")
   }.setup()

   val inputSlot = IOSlot(4, placeholder = ItemStack(Material.HOPPER))
      .inputFilter {
         it.type == Material.APPLE
      }.onInput {
         getViewers().lastOrNull()?.sendColorMessage("&a 放入了苹果")
         info("输入了苹果")
         messageButton.displayName = "&a强化苹果".toColor()
         messageButton.onClicked = {
            it.whoClicked.sendColorMessage("&a 你强化了苹果")
         }
      }.outputFilter {
         getViewers().lastOrNull()?.sendColorMessage("无法输出")
         false
      }.setup()
}
```

![image](https://user-images.githubusercontent.com/65019366/189704546-8651a8bb-2a68-4973-8133-51f5a987d154.png)

### 数据库

核心内置了 MySQL、MariaDB、SQLite、H2、Oracle、PostgreSQL、SQLServer 的支持,可以无缝切换

数据库使用了Exposed: https://github.com/JetBrains/Exposed

以下摘自Exposed的Readme

~~~ kotlin
import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val name = varchar("name", 50).index()
    val city = reference("city", Cities)
    val age = integer("age")
}

object Cities: IntIdTable() {
    val name = varchar("name", 50)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var name by Users.name
    var city by City referencedOn Users.city
    var age by Users.age
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)

    var name by Cities.name
    val users by User referrersOn Users.city
}

fun main() {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create (Cities, Users)

        val stPete = City.new {
            name = "St. Petersburg"
        }

        val munich = City.new {
            name = "Munich"
        }

        User.new {
            name = "a"
            city = stPete
            age = 5
        }

        User.new {
            name = "b"
            city = stPete
            age = 27
        }

        User.new {
            name = "c"
            city = munich
            age = 42
        }

        println("Cities: ${City.all().joinToString {it.name}}")
        println("Users in ${stPete.name}: ${stPete.users.joinToString {it.name}}")
        println("Adults: ${User.find { Users.age greaterEq 18 }.joinToString {it.name}}")
    }
}
~~~

生成的SQL

~~~ sql
    SQL: CREATE TABLE IF NOT EXISTS Cities (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(50) NOT NULL, CONSTRAINT pk_Cities PRIMARY KEY (id))
    SQL: CREATE TABLE IF NOT EXISTS Users (id INT AUTO_INCREMENT NOT NULL, name VARCHAR(50) NOT NULL, city INT NOT NULL, age INT NOT NULL, CONSTRAINT pk_Users PRIMARY KEY (id))
    SQL: CREATE INDEX Users_name ON Users (name)
    SQL: ALTER TABLE Users ADD FOREIGN KEY (city) REFERENCES Cities(id)
    SQL: INSERT INTO Cities (name) VALUES ('St. Petersburg'),('Munich')
    SQL: SELECT Cities.id, Cities.name FROM Cities
    Cities: St. Petersburg, Munich
    SQL: INSERT INTO Users (name, city, age) VALUES ('a', 1, 5),('b', 1, 27),('c', 2, 42)
    SQL: SELECT Users.id, Users.name, Users.city, Users.age FROM Users WHERE Users.city = 1
    Users in St. Petersburg: a, b
    SQL: SELECT Users.id, Users.name, Users.city, Users.age FROM Users WHERE Users.age >= 18
    Adults: b, c
~~~

### 其他

~~~ kotlin
// 快速注册一个事件
listen<PlayerLoginEvent> {
    player.sendColorMessage("#66ccff你好${player.name}")
}
// 添加一个BukkitTask
submit(delay = 10, period = 20, async = true) {
    println(1)
}
// 异步任务
runAsync {
    println("异步任务")
}
// 物品序列化与反序列化
val itemStack = ItemStack(Material.ANVIL)
        
val section = itemStack.toSection()
ItemUtils.fromSection(section)
        
val json = itemStack.toJson()
ItemUtils.fromJson(json)
        
val base64 = itemStack.toBase64()
ItemUtils.fromBase64ToItemStack(base64)
        
val byteArray = itemStack.toByteArray()
ItemUtils.fromByteArray(byteArray)

val listOf = listOf(ItemStack(Material.ANVIL), ItemStack(Material.ANVIL))
val toBase64 = listOf.toBase64()
ItemUtils.fromBase64ToItems(toBase64)

...
~~~



## 快速上手

### 环境需求

* Jdk 8
* IntelliJ IDEA 最新版
* IDEA Kotlin 插件
* 较好的网络环境 (下载依赖)

### 项目搭建

#### 方式一(推荐)

打开项目链接 -> 登录GitHub账号 -> 点击右上角的 `Use this template` 将项目复制到你的仓库里，使用你的项目仓库地址在IDEA中打开

#### 方式二

打开项目链接 -> 登录GitHub账号 -> 点击右上角的 `Fork` 按钮将项目fork到你的仓库中, 其他步骤同上，此方式的区别是可以**
同步上游仓库的修改**

#### 方式三

打开项目链接 -> 点击 `Code`按钮 -> 在下拉菜单中点击 `Download ZIP` 按钮将代码下载到本地，使用IDEA打开

### 项目设置

1. 打开项目后等待依赖下载完毕，在IDEA的左上角菜单中的 `文件` -> `项目结构` -> 将`项目SDK`设置为`jdk8`

2. 在IDEA的左上角菜单中的 `文件` -> `设置` 中将项目文件编码设置为 `UTF-8`

   ![image](https://user-images.githubusercontent.com/65019366/189704626-d5f3f488-4997-4fe8-9286-cf63d390e546.png)

### 项目结构

![image](https://user-images.githubusercontent.com/65019366/189704669-881303e3-3032-4763-bb81-2c57ba4b6217.png)

### 配置插件

如上图项目结构，在 `gradle.properties`文件中设置插件的基本信息

~~~properties
# 插件信息
pluginName=TemplatePlugin
# 包名
group=com.example.bukkit.templateplugin
# 作者
author=Iseason
# jar包输出路径
jarOutputFile=E:\\mc\\1.19\\plugins
# 插件版本
version=1.0
# api设置
kotlinVersion=1.7.10
shadowJarVersion=7.1.2
# 编译设置
# 是否混淆
obfuscated=false
# 是否删除未使用代码
shrink=true
# exposed 数据库框架版本
exposedVersion=0.39.2
kotlin.code.style=official
kotlin.incremental=true
kotlin.incremental.java=true
kotlin.caching.enabled=true
kotlin.parallel.tasks.in.project=true
org.gradle.caching=true
org.gradle.parallel=true
~~~

插件的代码位于`plugin`模块内，请确保插件主类继承`top.iseason.bukkittemplate.BukkitPlugin`类并声明为Object单例模式

包名应该为 `group` 类名应为`pluginName` (gradle.properties中的键值)

`com.example.bukkit.templateplugin`是一个实例插件，你可以参考里面的代码实现

**添加运行依赖**

项目依赖请在plugin模块下的 build.gradle.kts 添加,默认依赖如下

~~~ kotlin
dependencies {
    api(project(":core"))
//    反射库
//    compileOnly(kotlin("reflect"))

//    协程库
//    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")

    // 本地依赖放在libs文件夹内
    compileOnly(fileTree("libs") { include("*.jar") })
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
}
~~~

如果你想动态加载依赖，请在`plugin.yml`文件中添加，格式如下

~~~ yaml
name: ${name}
main: ${main}
version: ${version}
authors: [ ${ author } ]
api-version: 1.13
# 依赖管理
runtime-libraries:
  # 依赖的存放路径
  # 如果以@Plugin开头则将以插件配置文件夹为起点
  libraries-folder: 'libraries'
  # 远程仓库，按顺序检索可用的包
  repositories:
    - https://maven.aliyun.com/repository/public/
    - https://repo.maven.apache.org/maven2/
  # 依赖 group:artifact:version 格式,默认添加了Kotlin的运行依赖
  # ${kotlinVersion}为编译时替换的占位符，等于kotlin的版本，在gradle.properties 中修改
  libraries:
    - org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}
#    - org.jetbrains.exposed:exposed-core:${exposedVersion}
#    - org.jetbrains.exposed:exposed-dao:${exposedVersion}
#    - org.jetbrains.exposed:exposed-jdbc:${exposedVersion}
#    - org.jetbrains.exposed:exposed-java-time:${exposedVersion}
#    - com.zaxxer:HikariCP:4.0.3
#    - org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}
~~~

### 构建项目

#### 命令行

`gradle build` 或 `gradlew.bat build`

#### IDEA

![image](https://user-images.githubusercontent.com/65019366/189704739-db3e8e2f-cefa-46d2-9bac-504b5ce7af87.png)

jar包输出路径在 `gradle.properties`中修改





