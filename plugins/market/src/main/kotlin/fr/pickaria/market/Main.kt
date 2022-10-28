package fr.pickaria.market

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("value")?.setExecutor(GetValueCommand())

		logger.info("Market plugin loaded!")
	}
}
