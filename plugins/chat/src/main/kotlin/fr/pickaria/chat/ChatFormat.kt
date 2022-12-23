package fr.pickaria.chat

import io.papermc.paper.chat.ChatRenderer
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


internal class ChatFormat : Listener {
	companion object {
		val renderer = ChatRenderer { _: Player, sourceDisplayName: Component, message: Component, _: Audience ->
			sourceDisplayName.append(Config.chatFormat)
				.append(message.color(NamedTextColor.WHITE))
		}
	}

	@EventHandler
	fun onAsyncChat(event: AsyncChatEvent) {
		event.renderer(renderer)
	}
}