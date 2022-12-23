package fr.pickaria

import fr.pickaria.controller.initCommandManager
import fr.pickaria.controller.potion.runnable
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
import fr.pickaria.vue.job.ExperienceListener
import fr.pickaria.vue.job.JobCommand
import fr.pickaria.vue.job.jobMenu
import fr.pickaria.vue.market.*
import fr.pickaria.vue.potion.PotionCommand
import fr.pickaria.vue.potion.PotionListener
import fr.pickaria.vue.reforge.EnchantListeners
import fr.pickaria.vue.reward.RewardCommand
import fr.pickaria.vue.reward.RewardListeners
import fr.pickaria.vue.shard.GrindstoneListeners
import fr.pickaria.vue.shop.PlaceShop
import fr.pickaria.vue.shop.ShopListeners
import org.bukkit.Bukkit
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

		// Potion
		PotionCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		manager.registerCommand(PotionCommand())
		server.pluginManager.registerEvents(PotionListener(), this)
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 0, 20)

		// Reward
		RewardCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		manager.registerCommand(RewardCommand())
		server.pluginManager.registerEvents(RewardListeners(), this)

		// Jobs
		val jobCommand = JobCommand()
		getCommand("job")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")
		getCommand("jobs")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")
		jobMenu()
		server.pluginManager.registerEvents(ExperienceListener(this), this)
	}
}