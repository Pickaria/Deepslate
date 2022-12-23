package fr.pickaria

import fr.pickaria.controller.acf.initCommandManager
import fr.pickaria.model.datasources.openDatabase
import fr.pickaria.vue.PingCommand
import fr.pickaria.vue.chat.ChatFormat
import fr.pickaria.vue.chat.Motd
import fr.pickaria.vue.chat.PlayerJoin
import fr.pickaria.vue.economy.BalanceTopCommand
import fr.pickaria.vue.economy.MoneyCommand
import fr.pickaria.vue.economy.PayCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		saveDefaultConfig()

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
	}
}