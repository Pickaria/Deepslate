package fr.pickaria.vue.chat

import fr.pickaria.model.chat.chatConfig
import fr.pickaria.shared.updateDisplayName
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

internal class PlayerJoin : Listener {
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		with(event) {
			player.updateDisplayName()

			if (!player.hasPermission("pickaria.messages.join")) {
				joinMessage(null)
				// TODO: Log join message to admin players
			} else {
				joinMessage(chatConfig.join.append(player.displayName()))
			}

			val header = Component.text("Pickaria", NamedTextColor.GOLD, TextDecoration.BOLD)
				.append(Component.newline())
				.append(Component.text("PLAY.PICKARIA.FR", NamedTextColor.WHITE, TextDecoration.BOLD))
				.append(Component.newline())

			player.sendPlayerListHeader(header)
		}
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		with(event) {
			player.updateDisplayName()

			if (!player.hasPermission("pickaria.messages.quit")) {
				quitMessage(null)
				// TODO: Log join message to admin players
			} else {
				quitMessage(chatConfig.quit.append(player.displayName()))
			}
		}
	}

	@EventHandler
	fun onPlayer(event: PlayerAdvancementDoneEvent) {
		with(event) {
			player.updateDisplayName()

			if (!player.hasPermission("pickaria.messages.advancement")) {
				message(null)
			}
		}
	}
}