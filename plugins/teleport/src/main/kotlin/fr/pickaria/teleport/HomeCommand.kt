package fr.pickaria.teleport

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player


class HomeCommand : CommandExecutor, TabCompleter {
	companion object {
		const val NAME = "home"
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val homeName = try {
				args[0]
			} catch (_: ArrayIndexOutOfBoundsException) {
				NAME
			}

			val location = homeController.getHomeByName(sender.uniqueId, homeName)
			if (location != null) {
				if (location.block.type.isOccluding) {
					sender.sendMessage("§cCe point de teleportation n'est pas sécurisé, vous ne pouvez pas y être téléporté.")
				} else {
					teleportController.cooldownTeleport(sender, location)
				}
			} else {
				sender.sendMessage("§cCe point de teleportation n'existe pas.")
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