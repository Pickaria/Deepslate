package fr.pickaria.controller.economy

import fr.pickaria.Main
import fr.pickaria.model.economy.economyConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

fun initVaultCurrency() {
	economyConfig.currencies[economyConfig.vaultCurrency]?.let {
		val plugin = JavaPlugin.getProvidingPlugin(Main::class.java)
		Bukkit.getServicesManager().register(Economy::class.java, it.economy, plugin, ServicePriority.Normal)
	}
}