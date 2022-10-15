package fr.pickaria.teleport

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
					sender.sendMessage("§6Demande de téléportation acceptée.")
					it.first.sendMessage("§6Demande de téléportation acceptée.")
				} else {
					teleportController.cooldownTeleport(it.first, sender.location)
					sender.sendMessage("§6Demande de téléportation acceptée.")
					it.first.sendMessage("§6Demande de téléportation acceptée.")
				}

				teleportController.map.remove(sender)
			} ?: run {
				sender.sendMessage("§cAucune demande de téléportation en cours.")
			}
		}

		return true
	}

}
