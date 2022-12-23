package fr.pickaria.chat

import fr.pickaria.shared.updateDisplayName
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

internal class PlayerJoin : Listener {
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		event.player.let {
			it.updateDisplayName()
			event.joinMessage(Config.join.append(it.displayName()))
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		event.quitMessage(Config.quit.append(event.player.displayName()))
	}
}