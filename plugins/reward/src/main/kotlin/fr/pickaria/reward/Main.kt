package fr.pickaria.reward

import fr.pickaria.shared.setupEconomy
import net.milkbowl.vault.economy.Economy
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var economy: Economy
internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		setupEconomy()?.let { economy = it }

		saveDefaultConfig()
		Config.setConfig(config)
		namespace = NamespacedKey(this, "reward")

		getCommand("reward")?.setExecutor(RewardCommand())
		getCommand("placereward")?.setExecutor(PlaceShopCommand())
		server.pluginManager.registerEvents(RewardListeners(), this)

		rewardMenu()

		logger.info("Reward plugin loaded!")
	}
}
