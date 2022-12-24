package fr.pickaria.vue.job

import fr.pickaria.controller.job.bossBars
import fr.pickaria.controller.job.refreshDisplayName
import fr.pickaria.vue.job.jobs.*
import org.bukkit.Bukkit.getServer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class ExperienceListener(private val plugin: JavaPlugin) : Listener {
	init {
		getServer().pluginManager.run {
			registerEvents(this@ExperienceListener, plugin)
			registerEvents(JobListener(), plugin)

			registerEvents(Miner(), plugin)
			registerEvents(Hunter(), plugin)
			registerEvents(Farmer(), plugin)
			registerEvents(Breeder(), plugin)
			registerEvents(Alchemist(), plugin)
			registerEvents(Wizard(), plugin)
			registerEvents(Trader(), plugin)
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		bossBars.remove(event.player)
		event.player.refreshDisplayName()
	}

	/**
	 * Sets the suffix according to the player's job level.
	 */
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		event.player.refreshDisplayName()
	}
}