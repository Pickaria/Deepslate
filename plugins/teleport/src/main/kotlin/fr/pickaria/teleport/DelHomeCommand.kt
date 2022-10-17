package fr.pickaria.teleport

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


class DelHomeCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val homeName = try {
				args[0]
			} catch (_: ArrayIndexOutOfBoundsException) {
				SetHomeCommand.NAME
			}

			if (homeController.removeHome(sender.uniqueId, homeName)) {
				val message = miniMessage.deserialize(teleportConfig.homeDeleteMessage)
				sender.sendMessage(message)
			} else {
				val message = miniMessage.deserialize(teleportConfig.homeNotexistMessage)
				sender.sendMessage(message)
			}
		}
		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String> {
		return if (args.size == 1) {
			homeController.getHomeNames((sender as Player).uniqueId).filter { it.startsWith(args[0]) }
				.toMutableList()
		} else {
			mutableListOf()
		}
	}
}