package fr.pickaria.controller.reward

import fr.pickaria.model.reward.Config
import fr.pickaria.vue.reward.RewardCommand
import fr.pickaria.vue.reward.RewardListeners
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(config)
		namespace = NamespacedKey("pickaria", "reward")

		getCommand("reward")?.setExecutor(RewardCommand())
		server.pluginManager.registerEvents(RewardListeners(), this)

		logger.info("Reward plugin loaded!")
	}
}
