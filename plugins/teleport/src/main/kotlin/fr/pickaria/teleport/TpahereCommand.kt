package fr.pickaria.teleport

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit.getPlayer

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TpahereCommand : CommandExecutor {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			getPlayer(args[0])?.let {
				if (teleportController.map.contains(it)) {
					val message = miniMessage.deserialize(teleportConfig.teleportAlreadyError)
					sender.sendMessage(message)
				} else
				if (teleportController.createTpRequest(it, sender, true)) {
					val messageSender =miniMessage.deserialize(teleportConfig.teleportSentMessage)
					sender.sendMessage(messageSender)
					val messageTarget = miniMessage.deserialize(teleportConfig.teleportTheyMessage,Placeholder.unparsed("sender", sender.name))
					teleportController.sendTpRequestMessage(it, messageTarget )
				}
			}
		}

		return true
	}

}
