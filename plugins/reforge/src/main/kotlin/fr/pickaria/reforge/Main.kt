package fr.pickaria.reforge

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		namespace = NamespacedKey(this, "reforge")

		val reforge = ReforgeCommand()
		getCommand("reforge")?.setExecutor(reforge)
		server.pluginManager.registerEvents(reforge, this)
	}
}
