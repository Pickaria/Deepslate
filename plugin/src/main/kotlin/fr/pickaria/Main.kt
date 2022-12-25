package fr.pickaria

import fr.pickaria.controller.acf.enumCompletion
import fr.pickaria.controller.acf.limitCondition
import fr.pickaria.controller.initCommandManager
import fr.pickaria.controller.potion.runnable
import fr.pickaria.model.openDatabase
import fr.pickaria.model.potion.PotionType
import fr.pickaria.model.shop.ShopOffer
import fr.pickaria.model.town.BannerType
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
import fr.pickaria.vue.town.BannerCommand
import fr.pickaria.vue.town.BannerListeners
import fr.pickaria.vue.town.BookListeners
import fr.pickaria.vue.town.townMenu
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		saveDefaultConfig()

		enableBedrockLibrary()

		val manager = initCommandManager(this)
		openDatabase(dataFolder.absolutePath + "/database")

		// Events
		server.pluginManager.let {
			it.registerEvents(ArtefactListeners(), this)
			it.registerEvents(BannerListeners(this), this)
			it.registerEvents(BookListeners(), this)
			it.registerEvents(ChatFormat(), this)
			it.registerEvents(EnchantListeners(), this)
			it.registerEvents(ExperienceListener(this), this)
			it.registerEvents(GrindstoneListeners(), this)
			it.registerEvents(Motd(), this)
			it.registerEvents(PlayerJoin(), this)
			it.registerEvents(PotionListener(), this)
			it.registerEvents(RewardListeners(), this)
			it.registerEvents(ShopListeners(), this)
			it.registerEvents(SmithingListeners(), this)
		}

		// Command contexts
		BuyCommand.setupContext(manager)
		CancelOrderCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		JobCommand.setupContext(manager)
		RewardCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		SellCommand.setupContext(manager.commandCompletions)
		limitCondition(manager)

		// Command completions
		enumCompletion<BannerType>(manager, "banner_type", "Cette bannière n'existe pas.")
		enumCompletion<PotionType>(manager, "potion_type", "Cette potion n'existe pas.")
		enumCompletion<ShopOffer>(manager, "shop_type", "Ce magasin n'existe pas.")

		// Commands
		manager.registerCommand(BalanceTopCommand())
		manager.registerCommand(BannerCommand())
		manager.registerCommand(BuyCommand())
		manager.registerCommand(CancelOrderCommand())
		manager.registerCommand(FakeSellCommand())
		manager.registerCommand(JobCommand())
		manager.registerCommand(MarketCommand())
		manager.registerCommand(MoneyCommand())
		manager.registerCommand(PayCommand())
		manager.registerCommand(PingCommand())
		manager.registerCommand(PlaceShop())
		manager.registerCommand(PotionCommand())
		manager.registerCommand(RewardCommand())
		manager.registerCommand(SellCommand())

		// Menus
		foodMenu()
		homeMenu()
		jobMenu()
		ownOrdersMenu()
		townMenu()

		// Scheduler
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 0, 20)
	}
}