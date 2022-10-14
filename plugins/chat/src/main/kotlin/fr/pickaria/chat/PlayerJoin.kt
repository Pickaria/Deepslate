package fr.pickaria.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

internal class PlayerJoin : Listener {
	companion object {
		val join: Component = Component.text("[", NamedTextColor.GRAY)
			.append(Component.text("+", NamedTextColor.GOLD))
			.append(Component.text("]", NamedTextColor.GRAY))
			.append(Component.text(" ").color(NamedTextColor.WHITE))

		val quit: Component = Component.text("[", NamedTextColor.GRAY)
			.append(Component.text("-", NamedTextColor.RED))
			.append(Component.text("]", NamedTextColor.GRAY))
			.append(Component.text(" ").color(NamedTextColor.WHITE))
	}

	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		event.joinMessage(join.append(getPlayerDisplayName(event.player)))
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		event.quitMessage(quit.append(getPlayerDisplayName(event.player)))
	}
}