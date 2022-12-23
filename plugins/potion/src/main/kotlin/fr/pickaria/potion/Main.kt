package fr.pickaria.potion

import org.bukkit.Bukkit.getServicesManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var potionController: PotionController
internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		namespace = NamespacedKey("pickaria", "potion")
		Config.setConfig(config)

		val potionListener = PotionListener()

		getCommand("potion")?.setExecutor(potionListener)
		server.pluginManager.registerEvents(potionListener, this)

		potionController = PotionController(this)

		getServicesManager().register(PotionController::class.java, potionController, this, ServicePriority.Normal)

		logger.info("Potion plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		getServicesManager().unregisterAll(this)
	}
}
