package fr.pickaria.newmenu

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("newmenu")?.setExecutor(Command())
		server.pluginManager.registerEvents(Listeners(), this)

		homeMenu()
		testMenu()

		logger.info("Newmenu plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		unregister(DEFAULT_MENU)
	}
}
