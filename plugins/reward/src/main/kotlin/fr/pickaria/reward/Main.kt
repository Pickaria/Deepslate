package fr.pickaria.reward

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		namespace = NamespacedKey(this, "bundle")

		val crate = CrateCommand()
		getCommand("crate")?.setExecutor(crate)
		server.pluginManager.registerEvents(crate, this)

		logger.info("Reward plugin loaded!")
	}
}
