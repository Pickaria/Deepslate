package fr.pickaria.deepslate

import fr.pickaria.DEFAULT_MENU
import fr.pickaria.deepslate.home.foodMenu
import fr.pickaria.deepslate.home.homeMenu
import fr.pickaria.menu.unregister
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		homeMenu()
		foodMenu()
	}

	override fun onDisable() {
		super.onDisable()

		unregister(DEFAULT_MENU)
	}
}
