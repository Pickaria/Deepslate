package fr.pickaria.vue.job

import fr.pickaria.controller.job.bossBars
import fr.pickaria.controller.job.refreshDisplayName
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ExperienceListener : Listener {
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