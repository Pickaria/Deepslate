package fr.pickaria.shard

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		Config.setConfig(this.config)

		getCommand("place")?.setExecutor(PlaceShopCommand()) ?: server.logger.warning("Command `place` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(ShopListeners(), this)
		Bukkit.getServer().pluginManager.registerEvents(GrindstoneListeners(), this)
	}
}
