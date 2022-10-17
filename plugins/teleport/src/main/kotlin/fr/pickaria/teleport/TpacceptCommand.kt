package fr.pickaria.teleport

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TpacceptCommand : CommandExecutor {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			teleportController.map[sender]?.let {
				if (it.second) {
					teleportController.cooldownTeleport(sender, it.first.location)
					val messageSender = miniMessage.deserialize(teleportConfig.teleportAcceptMessage)
					sender.sendMessage(messageSender)
					val messageTarget = miniMessage.deserialize(teleportConfig.teleportAcceptMessage)
					it.first.sendMessage(messageTarget)
				} else {
					teleportController.cooldownTeleport(it.first, sender.location)
					val messageSender = miniMessage.deserialize(teleportConfig.teleportAcceptMessage)
					sender.sendMessage(messageSender)
					val messageTarget = miniMessage.deserialize(teleportConfig.teleportAcceptMessage)
					it.first.sendMessage(messageTarget)
				}

				teleportController.map.remove(sender)
			} ?: run {
				val message = miniMessage.deserialize(teleportConfig.teleportNotpMessage)
				sender.sendMessage(message)
			}
		}

		return true
	}

}
