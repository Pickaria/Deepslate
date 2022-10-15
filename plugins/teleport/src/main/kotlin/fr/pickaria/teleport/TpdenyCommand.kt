package fr.pickaria.teleport

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TpdenyCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			teleportController.map[sender]?.let {
				teleportController.map.remove(sender)
				it.first.sendMessage("§7Votre demande à été refusée ")
			} ?: run {
				sender.sendMessage("§cAucune demande de téléportation en cours")
			}
		}

		return true
	}
}
