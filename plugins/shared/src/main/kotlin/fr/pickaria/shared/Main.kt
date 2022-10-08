package fr.pickaria.shared

import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		logger.info("Shared plugin loaded!")

		openDatabase(dataFolder.absolutePath + "/database.db")
	}
}