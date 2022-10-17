package fr.pickaria.shop

import fr.pickaria.shared.setupEconomy
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin

internal val economy = setupEconomy()!!
internal lateinit var namespace: NamespacedKey

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		namespace = NamespacedKey(this, "pickarite")

		getCommand("test")?.setExecutor(TestCommand()) ?: server.logger.warning("Command `test` could not be registered")

		Bukkit.getServer().pluginManager.registerEvents(TestMenu(), this)

		logger.info("Shop plugin loaded!")
	}
}
