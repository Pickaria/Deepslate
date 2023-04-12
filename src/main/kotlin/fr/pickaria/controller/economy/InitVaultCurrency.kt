package fr.pickaria.controller.economy

import fr.pickaria.model.economy.economyConfig
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import net.milkbowl.vault.economy.Economy as VaultEconomy

fun JavaPlugin.initVaultCurrency() {
	economyConfig.currencies[economyConfig.vaultCurrency]?.let {
		Bukkit.getServicesManager().register(VaultEconomy::class.java, it.economy, this, ServicePriority.Normal)
		Bukkit.getLogger().info("Economy registered currency `${economyConfig.vaultCurrency}`.")
	} ?: Bukkit.getLogger().warning("No vault currency registered.")
}
