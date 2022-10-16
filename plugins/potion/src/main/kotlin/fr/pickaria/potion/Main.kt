package fr.pickaria.potion

import org.bukkit.plugin.java.JavaPlugin

lateinit var potionController: PotionController

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		val test = TestCommand(this)

		getCommand("potion")?.setExecutor(test)
		server.pluginManager.registerEvents(test, this)

		potionController = PotionController(this)

		logger.info("Potion plugin loaded!")
	}
}
