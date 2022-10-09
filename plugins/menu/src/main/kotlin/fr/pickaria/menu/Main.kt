package fr.pickaria.menu

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import org.bukkit.plugin.java.JavaPlugin

class Main: JavaPlugin() {
	companion object {
		lateinit var menuController: MenuController
	}

	override fun onEnable() {
		super.onEnable()

		menuController = MenuController(this)

		getCommand("menu")?.setExecutor(MenuCommand()) ?: logger.warning("Could not register menu command.")

		server.pluginManager.registerEvents(menuController, this)

		logger.info("Menu plugin loaded!")
	}
}

@OptIn(DelicateCoroutinesApi::class)
val Dispatchers.Menus: ExecutorCoroutineDispatcher
	get() = newSingleThreadContext("Menus")