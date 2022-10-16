package fr.pickaria.potion

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

lateinit var potionController: PotionController
lateinit var potionConfig: PotionConfig
internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		namespace = NamespacedKey(this, "potion")
		potionConfig = PotionConfig(this.config)

		val test = TestCommand()

		getCommand("potion")?.setExecutor(test)
		server.pluginManager.registerEvents(test, this)

		potionController = PotionController(this)

		logger.info("Potion plugin loaded!")
	}
}
