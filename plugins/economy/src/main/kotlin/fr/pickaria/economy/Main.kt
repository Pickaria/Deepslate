package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var economy: Economy
internal val miniMessage: MiniMessage = MiniMessage.miniMessage();

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(this.config)

		setupEconomy()

		getCommand("money")?.setExecutor(MoneyCommand()) ?: logger.warning("Could not register `money` command.")
		getCommand("balance")?.setExecutor(MoneyCommand()) ?: logger.warning("Could not register `balance` command.")
		getCommand("bal")?.setExecutor(MoneyCommand()) ?: logger.warning("Could not register `bal` command.")

		getCommand("pay")?.setExecutor(PayCommand()) ?: logger.warning("Could not register `pay` command.")

		server.pluginManager.registerEvents(Listeners(), this)

		if (economy is fr.pickaria.economy.Economy) {
			getCommand("balancetop")?.setExecutor(BalanceTopCommand())
				?: logger.warning("Could not register `balancetop` command.")
			getCommand("baltop")?.setExecutor(BalanceTopCommand())
				?: logger.warning("Could not register `baltop` command.")
		} else {
			logger.info("Pickaria is not handling economy, balance top feature is disabled.")
		}

		logger.info("Economy plugin loaded!")
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
			economy = Credit.economy
			Bukkit.getServicesManager().register(Economy::class.java, Credit.economy, this, ServicePriority.Normal)

			logger.info("Pickaria is handling economy")
		}

		return true
	}
}