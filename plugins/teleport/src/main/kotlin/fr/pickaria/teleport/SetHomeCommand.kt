package fr.pickaria.teleport

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SetHomeCommand : CommandExecutor, TabCompleter {
	companion object {
		const val NAME = "home"
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val location = sender.location

			if (location.block.type.isOccluding) {
				val message = miniMessage.deserialize(teleportConfig.homeNotsafecreateError)
				sender.sendMessage(message)
				return true
			}

			val homeName = try {
				args[0]
			} catch (_: ArrayIndexOutOfBoundsException) {
				NAME
			}

			homeController.addHome(sender.uniqueId, homeName, location)
			val message = miniMessage.deserialize(teleportConfig.homeCreateMessage)
			sender.sendMessage(message)
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String> {
		return mutableListOf()
	}
}