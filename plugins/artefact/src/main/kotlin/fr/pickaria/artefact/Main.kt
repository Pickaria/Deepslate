package fr.pickaria.artefact

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		val test = ArtefactCommand(this)
		getCommand("artefact")?.setExecutor(test)
		server.pluginManager.registerEvents(test, this)

		logger.info("Artefact plugin loaded!")
	}
}
