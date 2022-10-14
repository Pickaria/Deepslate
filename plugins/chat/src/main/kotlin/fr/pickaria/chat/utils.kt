package fr.pickaria.chat

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player


internal fun getPlayerDisplayName(player: Player) =
	chat?.let {
		val prefix = it.getPlayerPrefix(player)
		Component.text("$prefix${player.name}")
	} ?: player.displayName()

internal fun getPlayerName(player: Player) =
	chat?.let {
		val prefix = it.getPlayerPrefix(player)
		"$prefix${player.name}"
	} ?: player.name