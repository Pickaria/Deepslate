package fr.pickaria.job

import fr.pickaria.menu.menuController
import org.bukkit.plugin.java.JavaPlugin

lateinit var jobController: JobController
internal lateinit var jobConfig: JobConfig

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()

		jobConfig = JobConfig(this.config)

		jobController = JobController(this)

		val jobCommand = JobCommand()
		getCommand("job")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")
		getCommand("jobs")?.setExecutor(jobCommand) ?: server.logger.warning("Command job could not be registered")

		menuController.registerMenu("job", JobMenuFactory())

		logger.info("Job plugin loaded!")
	}
}
