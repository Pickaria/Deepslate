package fr.pickaria.menu

import fr.pickaria.menu.sub.HomeMenu
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import org.bukkit.plugin.java.JavaPlugin

val menuController: MenuController = MenuController()
const val DEFAULT_MENU = "home"

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		menuController.register(DEFAULT_MENU, HomeMenu.Factory())

		getCommand("menu")?.setExecutor(MenuCommand()) ?: logger.warning("Could not register `menu` command.")

		server.pluginManager.registerEvents(menuController, this)

		logger.info("Menu plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		menuController.unregister(DEFAULT_MENU)
	}
}

@OptIn(DelicateCoroutinesApi::class)
val Dispatchers.Menus: ExecutorCoroutineDispatcher
	get() = newSingleThreadContext("Menus")