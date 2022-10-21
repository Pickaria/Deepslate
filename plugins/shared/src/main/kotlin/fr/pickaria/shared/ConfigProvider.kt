package fr.pickaria.shared

import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import kotlin.reflect.KProperty

val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()

fun String.toSnakeCase(): String = camelRegex.replace(this) {
	"_${it.value}"
}.lowercase()

class FileConfigurationDelegator {
	var config: FileConfiguration? = null

	inline operator fun <reified T> getValue(thisRef: ConfigProvider, property: KProperty<*>): T =
		with(property.name.toSnakeCase()) {
			when (T::class) {
				Component::class -> config?.getString(this)?.let { Component.text(it) } as? T
				else -> config?.get(this) as? T
			} ?: throw Exception("Property $this not found in config file.")
		}
}

abstract class ConfigProvider(val config: FileConfigurationDelegator = FileConfigurationDelegator()) {
	fun setConfig(config: FileConfiguration) {
		this.config.config = config
	}
}