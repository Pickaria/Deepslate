package fr.pickaria.chat

import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		Config.setConfig(config)

		server.pluginManager.let {
			it.registerEvents(PlayerJoin(), this)
			it.registerEvents(ChatFormat(), this)
			it.registerEvents(Motd(), this)
		}

		logger.info("Chat plugin loaded!")
	}
}
