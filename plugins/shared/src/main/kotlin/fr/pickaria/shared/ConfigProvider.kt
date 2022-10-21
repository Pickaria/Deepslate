package fr.pickaria.shared

import net.kyori.adventure.text.Component
import org.bukkit.configuration.file.FileConfiguration
import kotlin.reflect.KProperty

infix fun FileConfiguration.getComponent(path: String): Component? = getString(path)?.let { Component.text(it) }
fun String.toComponent(): Component = Component.text(this)

class FileConfigurationDelegator {
	var config: FileConfiguration? = null

	inline operator fun <reified T> getValue(thisRef: ConfigProvider, property: KProperty<*>): T =
		config?.get(property.name) as? T ?: throw Exception("Config file not initialized")
}

abstract class ConfigProvider(val config: FileConfigurationDelegator = FileConfigurationDelegator()) {
	fun setConfig(config: FileConfiguration) {
		this.config.config = config
	}
}