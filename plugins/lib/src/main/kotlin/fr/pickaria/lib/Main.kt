package fr.pickaria.lib

import fr.pickaria.enableBedrockLibrary
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		enableBedrockLibrary()
	}
}
