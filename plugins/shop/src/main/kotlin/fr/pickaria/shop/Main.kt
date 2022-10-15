package fr.pickaria.shop

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("test")?.setExecutor(TestCommand()) ?: server.logger.warning("Command `test` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(TestMenu(), this)

		logger.info("Shop plugin loaded!")
	}
}
