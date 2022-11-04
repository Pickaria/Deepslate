package fr.pickaria.artefact

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit.getServicesManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var artefactNamespace: NamespacedKey
internal lateinit var receptacleNamespace: NamespacedKey
internal val miniMessage = MiniMessage.miniMessage()
internal lateinit var artefactConfig: ArtefactConfig


class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		artefactConfig = ArtefactConfig(this.config)

		getServicesManager().register(ArtefactConfig::class.java, artefactConfig, this, ServicePriority.Normal)

		artefactNamespace = NamespacedKey(this, "artefact")
		receptacleNamespace = NamespacedKey(this, "receptacle")

		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		logger.info("Artefact plugin loaded!")
	}
}
