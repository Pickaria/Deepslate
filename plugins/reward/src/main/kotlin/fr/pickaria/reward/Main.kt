package fr.pickaria.reward

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(config)
		namespace = NamespacedKey(this, "reward")

		getCommand("reward")?.setExecutor(RewardCommand())
		server.pluginManager.registerEvents(RewardListeners(), this)

		logger.info("Reward plugin loaded!")
	}
}
