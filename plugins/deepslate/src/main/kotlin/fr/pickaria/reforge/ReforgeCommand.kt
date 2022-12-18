package fr.pickaria.reforge

import fr.pickaria.shared.give
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class ReforgeCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			sender.give(getAttributeItem())
		}

		return true
	}
}