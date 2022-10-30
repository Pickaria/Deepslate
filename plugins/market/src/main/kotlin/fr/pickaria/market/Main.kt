package fr.pickaria.market

import fr.pickaria.menu.menuController
import fr.pickaria.shared.setupEconomy
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var economy: Economy

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		setupEconomy()?.let {
			economy = it
		}

		getCommand("sell")?.setExecutor(CreateSellOrderCommand())
		getCommand("buy")?.setExecutor(CreateBuyOrderCommand())
		getCommand("market")?.setExecutor(MarketCommand())
		getCommand("cancel")?.setExecutor(CancelOrderCommand())
		menuController.register("market", OrderListingMenu.Factory())

		logger.info("Market plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()
		menuController.unregister("shop")
	}
}
