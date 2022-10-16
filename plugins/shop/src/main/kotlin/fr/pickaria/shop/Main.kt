package fr.pickaria.shop

import fr.pickaria.shared.setupEconomy
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal val economy = setupEconomy()!!

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("test")?.setExecutor(TestCommand()) ?: server.logger.warning("Command `test` could not be registered")
		getCommand("shop")?.setExecutor(AnvilCommand(this)) ?: server.logger.warning("Command `shop` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(TestMenu(), this)

		logger.info("Shop plugin loaded!")
	}
}
