package fr.pickaria.shard

import fr.pickaria.artefact.ArtefactConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var namespace: NamespacedKey
internal val miniMessage = MiniMessage.miniMessage()
internal lateinit var shopConfig: ShopConfig
internal val artefactConfig: ArtefactConfig? = try {
	Bukkit.getServicesManager().getRegistration(ArtefactConfig::class.java)?.provider
} catch (_: NoClassDefFoundError) {
	null
}

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		shopConfig = ShopConfig(this.config)
		namespace = NamespacedKey(this, "pickarite")

		getCommand("place")?.setExecutor(PlaceShopCommand()) ?: server.logger.warning("Command `place` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(ShopListeners(), this)
		Bukkit.getServer().pluginManager.registerEvents(GrindstoneListeners(), this)

		logger.info("Shop plugin loaded!")
	}
}
