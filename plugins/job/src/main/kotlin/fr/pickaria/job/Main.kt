package fr.pickaria.job

import fr.pickaria.menu.menuController
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin

lateinit var jobController: JobController
internal lateinit var jobConfig: JobConfig

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		menuController.register("job", JobMenu.Factory())

		jobConfig = JobConfig(this.config)

		jobController = JobController(this)

		val jobCommand = JobCommand()
		getCommand("job")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")
		getCommand("jobs")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")

		logger.info("Job plugin loaded!")
	}

	override fun onDisable() {
		super.onDisable()

		menuController.unregister("job")
	}
}
