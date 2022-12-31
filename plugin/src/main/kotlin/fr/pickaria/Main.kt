package fr.pickaria

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import fr.pickaria.controller.economy.initVaultCurrency
import fr.pickaria.controller.libraries.acf.enumCompletion
import fr.pickaria.controller.libraries.acf.initCommandManager
import fr.pickaria.controller.libraries.acf.limitCondition
import fr.pickaria.controller.libraries.acf.registerCommands
import fr.pickaria.controller.libraries.bukkit.registerEvents
import fr.pickaria.controller.potion.runnable
import fr.pickaria.model.openDatabase
import fr.pickaria.model.potion.PotionType
import fr.pickaria.model.rank.rankListener
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
import fr.pickaria.vue.home.homeMenu
import fr.pickaria.vue.job.ExperienceListener
import fr.pickaria.vue.job.JobCommand
import fr.pickaria.vue.job.JobListener
import fr.pickaria.vue.job.jobMenu
import fr.pickaria.vue.job.jobs.*
import fr.pickaria.vue.market.*
import fr.pickaria.vue.miniblocks.MiniBlockCommand
import fr.pickaria.vue.miniblocks.miniBlocksMenu
import fr.pickaria.vue.potion.PotionCommand
import fr.pickaria.vue.potion.PotionListener
import fr.pickaria.vue.rank.RankCommand
import fr.pickaria.vue.rank.rankMenu
import fr.pickaria.vue.reforge.EnchantListeners
import fr.pickaria.vue.reward.DailyRewardListeners
import fr.pickaria.vue.reward.RewardCommand
import fr.pickaria.vue.reward.RewardListeners
import fr.pickaria.vue.reward.rewardMenu
import fr.pickaria.vue.shard.GrindstoneListeners
import fr.pickaria.vue.shop.PlaceShop
import fr.pickaria.vue.shop.ShopListeners
import fr.pickaria.vue.town.BannerCommand
import fr.pickaria.vue.town.BannerListeners
import fr.pickaria.vue.town.BookListeners
import fr.pickaria.vue.town.townMenu
import org.bukkit.Bukkit


class Main : SuspendingJavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		saveDefaultConfig()

		enableBedrockLibrary()
		initVaultCurrency()

		val manager = initCommandManager()
		openDatabase(dataFolder.absolutePath + "/database")

		// Events
		registerEvents<Alchemist>()
		registerEvents<ArtefactListeners>()
		registerEvents<BannerListeners>()
		registerEvents<BookListeners>()
		registerEvents<Breeder>()
		registerEvents<ChatFormat>()
		registerEvents<DailyRewardListeners>()
		registerEvents<EnchantListeners>()
		registerEvents<ExperienceListener>()
		registerEvents<Farmer>()
		registerEvents<GrindstoneListeners>()
		registerEvents<Hunter>()
		registerEvents<JobListener>()
		registerEvents<Miner>()
		registerEvents<Motd>()
		registerEvents<PlayerJoin>()
		registerEvents<PotionListener>()
		registerEvents<RewardListeners>()
		registerEvents<ShopListeners>()
		registerEvents<SmithingListeners>()
		registerEvents<Trader>()
		registerEvents<Wizard>()
		rankListener()

		// Command completions
		manager.enumCompletion<BannerType>("banner_type", "Cette bannière n'existe pas.")
		manager.enumCompletion<PotionType>("potion_type", "Cette potion n'existe pas.")
		manager.enumCompletion<ShopOffer>("shop_type", "Ce magasin n'existe pas.")

		// Command contexts
		BuyCommand.setupContext(manager)
		CancelOrderCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		JobCommand.setupContext(manager)
		MiniBlockCommand.setupContext(manager)
		RewardCommand.setupContext(manager.commandContexts, manager.commandCompletions)
		SellCommand.setupContext(manager.commandCompletions)
		RankCommand.setupContext(manager)
		MoneyCommand.setupContext(manager)
		manager.limitCondition()

		// Commands
		manager.registerCommands(
			BalanceTopCommand(),
			BannerCommand(),
			BuyCommand(),
			CancelOrderCommand(),
			FakeSellCommand(),
			JobCommand(),
			MarketCommand(),
			MiniBlockCommand(),
			MoneyCommand(),
			PayCommand(),
			PingCommand(),
			PlaceShop(),
			PotionCommand(),
			RankCommand(),
			RewardCommand(),
			SellCommand(),
		)

		// Menus
		homeMenu()
		jobMenu()
		miniBlocksMenu()
		orderListingMenu()
		ownOrdersMenu()
		rewardMenu()
		townMenu()
		rankMenu()

		// Scheduler
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, 0, 20)
	}
}