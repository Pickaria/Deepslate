package fr.pickaria.artefact

import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var artefactNamespace: NamespacedKey
internal lateinit var receptacleNamespace: NamespacedKey


class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(config)

		artefactNamespace = NamespacedKey(this, "artefact")
		receptacleNamespace = NamespacedKey(this, "receptacle")

		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		logger.info("Artefact plugin loaded!")
	}
}
