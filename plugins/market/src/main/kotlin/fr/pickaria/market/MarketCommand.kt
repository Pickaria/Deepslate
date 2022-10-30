package fr.pickaria.market

import fr.pickaria.menu.menuController
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MarketCommand: CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			menuController.openMenu(sender, "market", null)
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>
	): MutableList<String> {
		return mutableListOf()
	}
}