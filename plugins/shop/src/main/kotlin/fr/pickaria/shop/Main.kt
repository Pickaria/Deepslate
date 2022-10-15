package fr.pickaria.shop

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("test")?.setExecutor(TestCommand()) ?: server.logger.warning("Command `test` could not be registered")
		getCommand("x")?.setExecutor(TestMenu()) ?: server.logger.warning("Command `x` could not be registered")

		logger.info("Shop plugin loaded!")
	}
}
