@file:Suppress("unused", "DEPRECATION", "MemberVisibilityCanBePrivate")

package top.iseason.bukkit.bukkittemplate.utils.bukkit

import com.google.gson.Gson
import io.github.bananapuncher714.nbteditor.NBTEditor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.BlockInventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.map.MapView
import org.bukkit.material.SpawnEgg
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionEffect
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import top.iseason.bukkit.bukkittemplate.utils.bukkit.StringUtils.toEffectString
import top.iseason.bukkit.bukkittemplate.utils.bukkit.StringUtils.toRGBString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream


object ItemUtils {

    private val UNUSED_NBT = listOf(
        "display",
        "Enchantments",
        "StoredEnchantments",
        "CustomPotionEffects",
        "AttributeModifiers",
//        "CanPlaceOn",
//        "CanDestroy",
        "HideFlags",
        "SkullOwner",
        "Unbreakable",
        "Damage",
        "generation",
        "BlockEntityTag",
        "Fireworks",
        "Potion",
        "map",
        "author",
        "pages",
        "title",
        "generation",
        "BucketVariantTag",
        "Charged",
        "ChargedProjectiles",

        )

    /**
     * 修改ItemMeta
     */
    inline fun <T : ItemStack> T.applyMeta(block: ItemMeta.() -> Unit): T {
        val itemMeta = itemMeta ?: return this
        block(itemMeta)
        this.itemMeta = itemMeta
        return this
    }

    /**
     * 减少物品数量，如果小于0则物品变为空气
     */
    fun ItemStack.subtract(count: Int) {
        val i = amount - count
        if (i <= 0) type = Material.AIR
        else amount = i
    }

    /**
     * 增加物品数量，返回溢出的数量
     */
    fun ItemStack.add(count: Int): Int {
        val i = amount + count
        return if (i >= maxStackSize) {
            amount = maxStackSize
            i - maxStackSize
        } else {
            amount = i
            0
        }
    }

    /**
     * 检查材质是否是空气
     */
    fun Material.checkAir(): Boolean = when (this.name) {
        "AIR",
        "VOID_AIR",
        "CAVE_AIR",
        "LEGACY_AIR" -> true

        else -> false
    }

    /**
     * 检查物品是否是空气.null也为空气
     */
    fun ItemStack?.checkAir(): Boolean = if (this == null) false else type.checkAir()

    /**
     * 物品转化为字节
     */
    fun ItemStack.toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            it.writeObject(this)
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 物品转为BASE64字符串
     */
    fun ItemStack.toBase64(): String = Base64.getEncoder().encodeToString(this.toByteArray())

    /**
     * 转为Base64字符串
     */
    fun ByteArray.toBase64(): String = Base64.getEncoder().encodeToString(this)

    /**
     * Base64字符串 转为 ByteArray
     */
    fun String.base64ToByteArray(): ByteArray = Base64.getDecoder().decode(this)

    /**
     * 一组物品转化为字节
     */
    fun Collection<ItemStack>.toByteArray(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            it.writeInt(size)
            for (item in this) {
                it.writeObject(item)
            }
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 背包物品转化为字节，保存位置
     */
    fun Map<Int, ItemStack>.toByteArrays(): ByteArray {
        val outputStream = ByteArrayOutputStream()
        BukkitObjectOutputStream(outputStream).use {
            forEach { (index, itemStack) ->
                it.writeInt(index)
                it.writeObject(itemStack)
            }
        }
        val gzipStream = ByteArrayOutputStream()
        GZIPOutputStream(gzipStream).use { it.write(outputStream.toByteArray()) }
        return gzipStream.toByteArray()
    }

    /**
     * 背包物品转化为Base64字符串，保存位置
     */
    fun Map<Int, ItemStack>.toBase64() = toByteArrays().toBase64()

    /**
     * 一组物品转化为BASE64字符串
     */
    fun Collection<ItemStack>.toBase64(): String = this.toByteArray().toBase64()

    /**
     * 物品转为Json文本
     */
    fun ItemStack.toJson(): String = NBTEditor.getNBTCompound(this).toJson()

    /**
     * 序列化为bukkit支持的配置
     * @param allowNested 是否允许嵌套解析(比如潜影盒)，只对容器有效，为false时将容器内容转为base64储存
     */
    fun ItemStack?.toSection(allowNested: Boolean = true): YamlConfiguration {
        val yaml = YamlConfiguration()
        if (this == null) return yaml
        yaml["material"] = type.toString()
        if (amount != 1) yaml["amount"] = amount
        if (!hasItemMeta()) return yaml
        with(itemMeta!!) {
            // 名字
            if (hasDisplayName()) yaml["name"] = displayName
            // lore
            if (hasLore()) yaml["lore"] = lore
            // 耐久
            if (this is Damageable) if (damage != 0) yaml["damage"] = damage
            // 附魔
            if (hasEnchants()) yaml.createSection("enchants", enchants.mapKeys { it.key.key })
            // flags
            val itemFlags = itemFlags
            if (itemFlags.isNotEmpty()) yaml["flags"] = itemFlags.map { it.name }
            when (this) {
                // 附魔书附魔
                is EnchantmentStorageMeta -> if (hasStoredEnchants()) yaml.createSection("stored-enchants",
                    storedEnchants.mapKeys { it.key.key })
                // 头颅
                is SkullMeta -> {
                    val texture = NBTEditor.getTexture(this@toSection)
                    if (texture != null) yaml["skull"] = texture
                    else if (hasOwner()) yaml["skull-owner"] = owner
                }
                // 皮革
                is LeatherArmorMeta -> yaml["color"] = color.toRGBString()
                // 药水
                is PotionMeta -> {
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)) {
                        yaml["base-effect"] =
                            "${basePotionData.type.name},${basePotionData.isExtended},${basePotionData.isUpgraded}"
                        if (customEffects.isNotEmpty())
                            yaml["effects"] = customEffects.map { it.toEffectString() }
                        if (hasColor()) yaml["color"] = color!!.toRGBString()
                    } else if (durability != 0.toShort()) {
                        val potion = Potion.fromItemStack(this@toSection)
                        yaml["base-effect"] = "${potion.type.name},${potion.hasExtendedDuration()},${potion.isSplash}"
                        yaml["level"] = potion.level
                    }
                }

                is BlockStateMeta -> {
                    val blockState = blockState
                    if (blockState is CreatureSpawner) {
                        yaml["spawner"] = blockState.spawnedType.name
                    } else if (blockState is BlockInventoryHolder) {
                        if (allowNested) {
                            val createSection = yaml.createSection("inventory")
                            blockState.inventory.forEachIndexed { index, item ->
                                if (item == null) return@forEachIndexed
                                createSection[index.toString()] = item.toSection()
                            }
                        } else {
                            yaml["inventory"] = buildMap {
                                blockState.inventory.forEachIndexed { index, item ->
                                    if (item == null) return@forEachIndexed
                                    this[index] = item
                                }
                            }.toBase64()
                        }

                    }
                }

                is FireworkMeta -> {
                    yaml["power"] = power
                    for ((index, effect) in effects.withIndex()) {
                        yaml["effects.$index.type"] = effect.type.name
                        val fwc: ConfigurationSection = yaml.getConfigurationSection("effects.$index")!!
                        fwc["flicker"] = effect.hasFlicker()
                        fwc["trail"] = effect.hasTrail()
                        val colors = fwc.createSection("colors")
                        colors["base"] = effect.colors.map { it.toRGBString() }
                        colors["fade"] = effect.fadeColors.map { it.toRGBString() }
                    }
                }

                is BookMeta -> {
                    val bookInfo = yaml.createSection("book")
                    bookInfo["title"] = title
                    bookInfo["author"] = author
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9)) {
                        bookInfo["generation"] = generation?.name
                    }
                    bookInfo["pages"] = pages
                }

                is MapMeta -> {
                    val mapSection = yaml.createSection("map")
                    mapSection["scaling"] = isScaling
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                        if (hasLocationName()) mapSection["location"] = locationName
                        if (hasColor()) {
                            mapSection["color"] = color?.toRGBString()
                        }
                    }
                    if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)) {
                        if (hasMapView()) {
                            val mapView: MapView = mapView!!
                            val view = mapSection.createSection("view")
                            view["scale"] = mapView.scale.toString()
                            view["world"] = mapView.world?.name
                            view["center"] = "${mapView.centerX},${mapView.centerZ}"
                            view["locked"] = mapView.isLocked
                            view["tracking-position"] = mapView.isTrackingPosition
                            view["unlimited-tracking"] = mapView.isUnlimitedTracking
                        }
                    }
                }

            }
            //老版本刷怪蛋 1.13 以下
            if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_13)) {
                if (NBTEditor.getMinecraftVersion().lessThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                    if (data is SpawnEgg) yaml["spawn-egg"] = (data as SpawnEgg).spawnedType.getName()
                } else if (this is SpawnEggMeta) {
                    yaml["spawn-egg"] = spawnedType.getName()
                }
            }
            // 1.9以上的属性
            if (NBTEditor.getMinecraftVersion()
                    .greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_9) && hasAttributeModifiers()
            ) {
                val mutableMapOf = mutableMapOf<String, Any>()
                attributeModifiers!!.forEach { t, u ->
                    val serialize = u.serialize()
                    serialize["operation"] = u.operation.name
                    mutableMapOf[t.name] = serialize
                }
                yaml.createSection("enchants", mutableMapOf)
            }
            // 1.11以上
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_11)) {
                // 无法破坏
                if (isUnbreakable) yaml["unbreakable"] = true
                // 旗帜
                if (this is BannerMeta) yaml.createSection(
                    "banner",
                    patterns.associate { it.pattern.name to it.color.name })
            } else if (this.spigot().isUnbreakable) yaml["unbreakable"] = true
            // 1.14 以上
            if (NBTEditor.getMinecraftVersion().greaterThanOrEqualTo(NBTEditor.MinecraftVersion.v1_14)) {
                // 模型
                if (hasCustomModelData()) yaml["custom-model-data"] = customModelData
                when (this) {
                    //弩
                    is CrossbowMeta -> {
                        if (chargedProjectiles.isNotEmpty())
                            for ((i, projectiles) in chargedProjectiles.withIndex()) {
                                yaml["projectiles.$i"] = projectiles.toSection()
                            }
                    }
                    // 热带鱼桶
                    is TropicalFishBucketMeta -> {
                        yaml["pattern"] = pattern.name
                        yaml["color"] = bodyColor.name
                        yaml["pattern-color"] = patternColor.name
                    }
                    // 谜之炖菜
                    is SuspiciousStewMeta -> {
                        yaml["effects"] = customEffects.map { it.toEffectString() }
                    }
                }
            }
        }
        // 额外的NBt
        val json = Gson().fromJson(NBTEditor.getNBTCompound(this, "tag").toJson(), Map::class.java).toMutableMap()
        for (s in UNUSED_NBT) {
            json.remove(s)
        }
        if (json.isNotEmpty()) {
            yaml["nbt"] = json
        }

        return yaml
    }

    /**
     * 字节转换为ItemStack
     */
    fun fromByteArray(bytes: ByteArray): ItemStack {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            BukkitObjectInputStream(it1).use { return it.readObject() as ItemStack }
        }
    }

    /**
     * BASE64字符串转为物品
     */
    fun fromBase64ToItemStack(base64: String) = fromByteArray(base64.base64ToByteArray())

    /**
     * 字节转换为一组List<ItemStack>
     */
    fun fromByteArrays(bytes: ByteArray): List<ItemStack> {
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            val mutableListOf = mutableListOf<ItemStack>()
            BukkitObjectInputStream(it1).use {
                val size = it.readInt()
                for (i in 0 until size) {
                    mutableListOf.add(it.readObject() as ItemStack)
                }
            }
            return mutableListOf
        }
    }

    /**
     * BASE64字符串转为List<ItemStack>
     */
    fun fromBase64ToItems(base64: String) = fromByteArrays(base64.base64ToByteArray())

    /**
     * ByteArrays转为 Map<Int, ItemStack>
     */
    fun fromByteArraysToMap(bytes: ByteArray): Map<Int, ItemStack> {
        val map = mutableMapOf<Int, ItemStack>()
        GZIPInputStream(ByteArrayInputStream(bytes)).use { it1 ->
            BukkitObjectInputStream(it1).use {
                runCatching {
                    while (true) {
                        map[it.readInt()] = it.readObject() as ItemStack
                    }
                }
            }
        }
        return map
    }

    /**
     * BASE64字符串转为 Map<Int, ItemStack>
     */
    fun fromBase64ToMap(base64: String) = fromByteArraysToMap(base64.base64ToByteArray())

    /**
     * 由json字符串转ItemStack
     */
    fun fromJson(json: String): ItemStack = NBTEditor.getItemFromTag(NBTEditor.getNBTCompound(json))

}

object StringUtils {
    fun Color.toRGBString(): String = "${red},${green},${blue}"
    fun PotionEffect.toEffectString(): String = "${type.name},${duration},${amplifier}"
}