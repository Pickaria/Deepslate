package fr.pickaria.economy

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var economy: Economy

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		logger.info("Economy plugin loaded!")

		setupEconomy()

		getCommand("money")?.setExecutor(MoneyCommand()) ?: logger.warning("Could not register `money` command.")
	}

	private fun setupEconomy(): Boolean {
		server.pluginManager.getPlugin("Vault") ?: run {
			logger.warning("VaultAPI not found, economy is not available")
			return false
		}

		server.servicesManager.getRegistration(Economy::class.java)?.let {
			economy = it.provider

			logger.info("Third party plugin is handling economy")
		} ?: let {
			economy = PickariaEconomy()
			Bukkit.getServicesManager().register(Economy::class.java, economy, this, ServicePriority.Normal)

			logger.info("Pickaria is handling economy")
		}

		return true
	}
}