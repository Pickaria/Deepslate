package fr.pickaria.chat

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


internal class ChatFormat: Listener {
	companion object {
		val renderer = ChatRenderer { player: Player, _: Component, message: Component, viewer: Audience ->
			getPlayerDisplayName(player).color(NamedTextColor.GRAY)
				.append(Component.text(" » ", NamedTextColor.GRAY))
				.append(message.color(NamedTextColor.WHITE))
		}
	}

	@EventHandler
	fun onAsyncPlayerChat(e: AsyncChatEvent) {
		e.renderer(renderer)
	}
}