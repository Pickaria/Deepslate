package fr.pickaria.menu

import fr.pickaria.menu.home.foodMenu
import fr.pickaria.menu.home.homeMenu
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		getCommand("menu")?.setExecutor(Command())
		server.pluginManager.registerEvents(Listeners(), this)

		homeMenu()
		foodMenu()
	}

	override fun onDisable() {
		super.onDisable()

		unregister(DEFAULT_MENU)
	}
}
