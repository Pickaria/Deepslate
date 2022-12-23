package fr.pickaria

import fr.pickaria.controller.initCommandManager
import fr.pickaria.model.openDatabase
import fr.pickaria.vue.PingCommand
import fr.pickaria.vue.artefact.ArtefactListeners
import fr.pickaria.vue.artefact.SmithingListeners
import fr.pickaria.vue.chat.ChatFormat
import fr.pickaria.vue.chat.Motd
import fr.pickaria.vue.chat.PlayerJoin
import fr.pickaria.vue.economy.BalanceTopCommand
import fr.pickaria.vue.economy.MoneyCommand
import fr.pickaria.vue.economy.PayCommand
import fr.pickaria.vue.home.foodMenu
import fr.pickaria.vue.home.homeMenu
import fr.pickaria.vue.market.*
import fr.pickaria.vue.reforge.EnchantListeners
import fr.pickaria.vue.shard.GrindstoneListeners
import fr.pickaria.vue.shop.PlaceShop
import fr.pickaria.vue.shop.ShopListeners
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		saveDefaultConfig()

		enableBedrockLibrary()

		val manager = initCommandManager(this)
		manager.registerCommand(PingCommand())

		// Chat
		server.pluginManager.let {
			it.registerEvents(PlayerJoin(), this)
			it.registerEvents(ChatFormat(), this)
			it.registerEvents(Motd(), this)
		}

		// Database
		openDatabase(dataFolder.absolutePath + "/database")

		// Economy
		manager.registerCommand(PayCommand())
		manager.registerCommand(MoneyCommand())
		manager.registerCommand(BalanceTopCommand())

		// Artefacts
		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		// Reforge
		server.pluginManager.registerEvents(EnchantListeners(), this)

		// Shards
		PlaceShop.setupContext(manager.commandContexts, manager.commandCompletions)
		manager.registerCommand(PlaceShop())

		server.pluginManager.registerEvents(ShopListeners(), this)
		server.pluginManager.registerEvents(GrindstoneListeners(), this)

		// Deepslate
		homeMenu()
		foodMenu()

		// Market
		getCommand("sell")?.setExecutor(CreateSellOrderCommand())
		getCommand("buy")?.setExecutor(BuyOrderCommand())
		manager.registerCommand(MarketCommand())
		manager.registerCommand(FakeSellCommand())
		getCommand("cancel")?.setExecutor(CancelOrderCommand())

		orderListingMenu()
		ownOrdersMenu()
	}
}