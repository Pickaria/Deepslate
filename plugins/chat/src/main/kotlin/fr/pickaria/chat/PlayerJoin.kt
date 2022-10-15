package fr.pickaria.chat

import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

internal class PlayerJoin : Listener {
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		event.player.let {
			it.displayName(getPlayerDisplayName(it))
			event.joinMessage(chatConfig.join.append(it.displayName()))
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		event.quitMessage(chatConfig.join.append(event.player.displayName()))
	}
}