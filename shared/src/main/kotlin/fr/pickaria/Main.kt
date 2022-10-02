package fr.pickaria

import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		logger.info("Menu plugin loaded!")
	}
}