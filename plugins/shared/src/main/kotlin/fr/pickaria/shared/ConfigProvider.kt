package fr.pickaria.shared

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.ConfigurationSection
import kotlin.reflect.KProperty

internal val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.toSnakeCase(): String = camelRegex.replace(this) {
	"_${it.value}"
}.lowercase()

abstract class ConfigProvider {
	var section: ConfigurationSection? = null
	val deserializer: MiniMessageDeserializer = MiniMessageDeserializer()

	inline operator fun <reified T> getValue(thisRef: ConfigProvider, property: KProperty<*>): T =
		with(property.name.toSnakeCase()) {
			when (T::class) {
				Component::class -> {
					section?.getString(this)?.let { Component.text(it) } as? T
				}

				else -> {
					section?.get(this) as? T
				}
			} ?: throw Exception("Property $this not found in config file.")
		}

	fun setConfig(config: ConfigurationSection) {
		this.section = config
	}

	inner class MiniMessageDeserializer {
		private val miniMessage: MiniMessage = MiniMessage.miniMessage()

		operator fun getValue(config: ConfigProvider, property: KProperty<*>): Component =
			section?.getString(property.name.toSnakeCase())?.let {
				miniMessage.deserialize(it)
			} ?: Component.text("")
	}
}