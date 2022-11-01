package fr.pickaria.job

import fr.pickaria.menu.menuController
import fr.pickaria.potion.PotionController
import fr.pickaria.shared.setupEconomy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit.getServicesManager
import org.bukkit.plugin.java.JavaPlugin

internal lateinit var jobController: JobController
internal lateinit var jobConfig: JobConfig
internal lateinit var economy: Economy
internal val miniMessage: MiniMessage = MiniMessage.miniMessage()
internal val potionController: PotionController? = try {
	getServicesManager().getRegistration(PotionController::class.java)?.provider
} catch (_: NoClassDefFoundError) {
	null
}

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		setupEconomy()?.let {
			economy = it

			saveDefaultConfig()

			menuController.register("job", JobMenu.Factory())

			jobConfig = JobConfig(this.config)

			jobController = JobController(this)

			val jobCommand = JobCommand()
			getCommand("job")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")
			getCommand("jobs")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")

			logger.info("Job plugin loaded!")
		} ?: run {
			logger.severe("Economy not found, disabling plugin!")
			server.pluginManager.disablePlugin(this)
		}
	}

	override fun onDisable() {
		super.onDisable()

		menuController.unregister("job")
	}
}
