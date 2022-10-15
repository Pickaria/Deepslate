package fr.pickaria.teleport

import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

val homeController: HomeController = HomeController()
val economy: Economy? = null
lateinit var teleportController: TeleportController

class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		teleportController = TeleportController(this)

		// Teleport commands
		getCommand("tprandom")?.setExecutor(RandomCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")

		// Home
		getCommand("sethome")?.setExecutor(SetHomeCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")
		getCommand("home")?.setExecutor(HomeCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")
		getCommand("delhome")?.setExecutor(DelHomeCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")

		// TPA
		getCommand("tpa")?.setExecutor(TpaCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")
		getCommand("tpahere")?.setExecutor(TpahereCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")
		getCommand("tpaccept")?.setExecutor(TpacceptCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")
		getCommand("tpdeny")?.setExecutor(TpdenyCommand()) ?: server.logger.log(Level.WARNING, "Command could not be registered")

		logger.info("Teleport plugin loaded!")
	}
}
