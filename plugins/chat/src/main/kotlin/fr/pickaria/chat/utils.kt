package fr.pickaria.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player


fun getPlayerDisplayName(player: Player) =
	chat?.let { chat ->
		val prefix = chat.getPlayerPrefix(player)?.let {
			miniMessage.deserialize(it)
				.append(Component.text(" "))
		} ?: Component.empty()

		val suffix = chat.getPlayerSuffix(player)?.let {
			Component.text(" ")
				.append(miniMessage.deserialize(it))
		} ?: Component.empty()

		prefix.append(player.name().color(NamedTextColor.WHITE)).append(suffix)
	} ?: player.displayName()
