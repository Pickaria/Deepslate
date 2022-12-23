package fr.pickaria.vue.chat

import fr.pickaria.model.chat.chatConfig
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
			event.joinMessage(chatConfig.join.append(it.displayName()))
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		event.quitMessage(chatConfig.quit.append(event.player.displayName()))
	}
}