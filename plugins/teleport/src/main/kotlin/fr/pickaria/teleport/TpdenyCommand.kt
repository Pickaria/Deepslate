package fr.pickaria.teleport

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TpdenyCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			teleportController.map[sender]?.let {
				teleportController.map.remove(sender)
				val message = miniMessage.deserialize(teleportConfig.teleportDenyMessage)
				it.first.sendMessage(message)
			} ?: run {
				val message = miniMessage.deserialize(teleportConfig.teleportNotpMessage)
				sender.sendMessage(message)
			}
		}

		return true
	}
}
