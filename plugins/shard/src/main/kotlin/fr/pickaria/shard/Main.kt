package fr.pickaria.shard

import fr.pickaria.artefact.ArtefactConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

internal val miniMessage = MiniMessage.miniMessage()
internal val artefactConfig: ArtefactConfig? = try {
	Bukkit.getServicesManager().getRegistration(ArtefactConfig::class.java)?.provider
} catch (_: NoClassDefFoundError) {
	null
}

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		Config.setConfig(this.config)

		getCommand("place")?.setExecutor(PlaceShopCommand()) ?: server.logger.warning("Command `place` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(ShopListeners(), this)
		Bukkit.getServer().pluginManager.registerEvents(GrindstoneListeners(), this)
	}
}
