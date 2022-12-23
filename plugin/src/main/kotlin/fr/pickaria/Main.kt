package fr.pickaria

import co.aikar.commands.PaperCommandManager
import fr.pickaria.model.datasources.openDatabase
import fr.pickaria.vue.PingCommand
import fr.pickaria.vue.chat.ChatFormat
import fr.pickaria.vue.chat.Motd
import fr.pickaria.vue.chat.PlayerJoin
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		val manager = PaperCommandManager(this)
		manager.registerCommand(PingCommand())

		saveDefaultConfig()

		// Chat
		server.pluginManager.let {
			it.registerEvents(PlayerJoin(), this)
			it.registerEvents(ChatFormat(), this)
			it.registerEvents(Motd(), this)
		}

		// Database
		openDatabase(dataFolder.absolutePath + "/database")
	}
}