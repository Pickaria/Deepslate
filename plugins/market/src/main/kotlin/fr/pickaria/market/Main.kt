package fr.pickaria.market

import fr.pickaria.market.menu.orderListingMenu
import fr.pickaria.market.menu.ownOrdersMenu
import fr.pickaria.menu.unregister
import fr.pickaria.shared.setupEconomy
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
		getCommand("buy")?.setExecutor(CreateBuyOrderCommand())
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
