package fr.pickaria.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player


internal fun getPlayerDisplayName(player: Player) =
	chat?.let {
		miniMessage.deserialize(it.getPlayerPrefix(player))
			.append(Component.text(" "))
			.append(player.name().color(NamedTextColor.WHITE))
	} ?: player.displayName()

internal fun getPlayerName(player: Player) =
	chat?.let {
		player.name
	} ?: player.name