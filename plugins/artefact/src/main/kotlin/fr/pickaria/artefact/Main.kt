package fr.pickaria.artefact

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		namespace = NamespacedKey(this, "artefact")

		val test = ArtefactCommand()
		getCommand("artefact")?.setExecutor(test)
		server.pluginManager.registerEvents(test, this)

		logger.info("Artefact plugin loaded!")
	}
}
