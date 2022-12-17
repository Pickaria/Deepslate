package fr.pickaria

import fr.pickaria.artefact.ArtefactListeners
import fr.pickaria.artefact.Config
import fr.pickaria.artefact.SmithingListeners
import fr.pickaria.reforge.EnchantListeners
import fr.pickaria.reforge.ReforgeCommand
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var artefactNamespace: NamespacedKey
internal lateinit var receptacleNamespace: NamespacedKey
internal lateinit var reforgeNamespace: NamespacedKey


class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()
		Config.setConfig(config)

		artefactNamespace = NamespacedKey(this, "artefact")
		receptacleNamespace = NamespacedKey(this, "receptacle")

		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		reforgeNamespace = NamespacedKey(this, "reforge")

		getCommand("reforge")?.setExecutor(ReforgeCommand())
		getCommand("placereforge")?.setExecutor(PlaceShopCommand())
		server.pluginManager.registerEvents(EnchantListeners(), this)
	}
}
