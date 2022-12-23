package fr.pickaria.legacy.market

import fr.pickaria.menu.unregister
import fr.pickaria.model.market.Config
import fr.pickaria.shared.setupEconomy
import fr.pickaria.vue.market.*
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var economy: Economy

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(this.config)

		setupEconomy()?.let {
			economy = it
		}

		getCommand("sell")?.setExecutor(CreateSellOrderCommand())
		getCommand("buy")?.setExecutor(BuyOrderCommand())
		getCommand("market")?.setExecutor(MarketCommand())
		getCommand("cancel")?.setExecutor(CancelOrderCommand())
		getCommand("fake")?.setExecutor(FakeSellCommand())
		orderListingMenu()
		ownOrdersMenu()

		logger.info("Market plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()
		unregister("market")
		unregister("orders")
	}
}
