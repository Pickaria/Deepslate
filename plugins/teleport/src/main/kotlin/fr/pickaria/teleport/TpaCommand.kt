package fr.pickaria.teleport

import org.bukkit.Bukkit.getPlayer

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TpaCommand : CommandExecutor {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			getPlayer(args[0])?.let {
				if (teleportController.map.contains(it)) {
					sender.sendMessage("§cUne demande est déjà en cours pour ce joueur.")
				} else
					if (teleportController.createTpRequest(it, sender, false)) {
						sender.sendMessage("§7Demande de téléportation envoyée à §6${it.name}§7.")
						teleportController.sendTpRequestMessage(it, "§6${sender.name}§7 souhaite se téléporter à vous.")
					}
			}
		}

		return true
	}

}
