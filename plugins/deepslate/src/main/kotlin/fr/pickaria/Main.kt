package fr.pickaria

import fr.pickaria.artefact.ArtefactListeners
import fr.pickaria.artefact.SmithingListeners
import fr.pickaria.home.foodMenu
import fr.pickaria.home.homeMenu
import fr.pickaria.menu.unregister
import fr.pickaria.reforge.EnchantListeners
import fr.pickaria.reforge.ReforgeCommand
import fr.pickaria.shard.GrindstoneListeners
import fr.pickaria.shard.ShopListeners
import fr.pickaria.shops.CreateShops
import org.bukkit.Bukkit
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

		// Artefacts
		artefactNamespace = NamespacedKey(this, "artefact")
		receptacleNamespace = NamespacedKey(this, "receptacle")

		server.pluginManager.registerEvents(ArtefactListeners(), this)
		server.pluginManager.registerEvents(SmithingListeners(), this)

		// Reforge
		reforgeNamespace = NamespacedKey(this, "reforge")

		getCommand("reforge")?.setExecutor(ReforgeCommand())
		getCommand("placereforge")?.setExecutor(PlaceShopCommand())
		server.pluginManager.registerEvents(EnchantListeners(), this)

		// Shards
		getCommand("placeshop")?.setExecutor(CreateShops())
			?: server.logger.warning("Command `place` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(ShopListeners(), this)
		Bukkit.getServer().pluginManager.registerEvents(GrindstoneListeners(), this)

		// Deepslate
		homeMenu()
		foodMenu()
	}

	override fun onDisable() {
		super.onDisable()

		unregister(DEFAULT_MENU)
	}
}
