package fr.pickaria.shared

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.loot.LootTable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.toSnakeCase(): String = camelRegex.replace(this) {
	"_${it.value}"
}.lowercase()

open class ConfigProvider(var section: ConfigurationSection? = null) {
	protected val miniMessageDeserializer: MiniMessageDeserializer by lazy {
		MiniMessageDeserializer()
	}
	protected val materialLoader: MaterialLoader by lazy {
		MaterialLoader()
	}
	protected val lootTableLoader: LootTableLoader by lazy {
		LootTableLoader()
	}

	protected inline fun <reified T : ConfigProvider> sectionLoader(): SectionLoader<T> = SectionLoader(T::class)

	protected inner class SectionLoader<T: ConfigProvider>(private val type: KClass<T>) {
		operator fun getValue(config: ConfigProvider, property: KProperty<*>): Map<String, T> =
			section?.getConfigurationSection(property.name.toSnakeCase())?.let {
				it.getKeys(false).associateWith { key ->
					type.constructors.first().call().apply {
						it.getConfigurationSection(key)?.let { config -> setConfig(config) }
					}
				}
			} ?: mapOf()
	}

	protected inline operator fun <reified T> getValue(thisRef: ConfigProvider, property: KProperty<*>): T =
		with(property.name.toSnakeCase()) {
			when (T::class) {
				Component::class -> {
					section?.getString(this)?.let { Component.text(it) } as? T
				}

				else -> {
					section?.get(this) as? T
				}
			} ?: throw Exception("Property `$this` not found in config file.")
		}

	fun setConfig(config: ConfigurationSection) {
		this.section = config
	}

	protected inner class MiniMessageDeserializer {
		private val miniMessage: MiniMessage = MiniMessage.miniMessage()

		operator fun getValue(config: ConfigProvider, property: KProperty<*>): Component =
			section?.getString(property.name.toSnakeCase())?.let {
				miniMessage.deserialize(it)
			} ?: Component.text("")
	}

	protected inner class MaterialLoader {
		operator fun getValue(config: ConfigProvider, property: KProperty<*>): Material =
			section?.getString(property.name.toSnakeCase())?.let {
				Material.getMaterial(it)
			}!!
	}

	protected inner class LootTableLoader {
		operator fun getValue(config: ConfigProvider, property: KProperty<*>): LootTable =
			section?.getString(property.name.toSnakeCase())?.let {
				val (namespace, key) = it.split(':')
				Bukkit.getLootTable(NamespacedKey(namespace, key))
			}!!
	}
}