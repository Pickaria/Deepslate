package fr.pickaria.teleport

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import net.milkbowl.vault.economy.Economy
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level


internal val homeController: HomeController = HomeController()
internal val economy: Economy? = null
internal val miniMessage = MiniMessage.miniMessage()
internal lateinit var teleportController: TeleportController
internal lateinit var teleportConfig: TeleportConfig


class Main : JavaPlugin() {
	override fun onEnable() {
		super.onEnable()

		saveDefaultConfig()


		teleportController = TeleportController(this)
		teleportConfig = TeleportConfig(this.config)


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
