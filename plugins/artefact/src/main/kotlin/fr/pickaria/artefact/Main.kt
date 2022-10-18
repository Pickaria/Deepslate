package fr.pickaria.artefact

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		namespace = NamespacedKey(this, "artefact")

		getCommand("artefact")?.setExecutor(ArtefactCommand())
		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		logger.info("Artefact plugin loaded!")
	}
}
