package fr.pickaria.lib

import fr.pickaria.enableBedrockLibrary
import fr.pickaria.spawner.enableSpawnerLibrary
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()
		enableBedrockLibrary()
		enableSpawnerLibrary()
	}
}
