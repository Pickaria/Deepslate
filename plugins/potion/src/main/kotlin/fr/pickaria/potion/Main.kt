package fr.pickaria.potion

import org.bukkit.Bukkit.getServicesManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var potionController: PotionController
internal lateinit var potionConfig: PotionConfig
internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		namespace = NamespacedKey(this, "potion")
		potionConfig = PotionConfig(this.config)

		val test = PotionListener()

		getCommand("potion")?.setExecutor(test)
		server.pluginManager.registerEvents(test, this)

		potionController = PotionController(this)

		getServicesManager().register(PotionController::class.java, potionController, this, ServicePriority.Normal)

		logger.info("Potion plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		getServicesManager().unregisterAll(this)
	}
}
