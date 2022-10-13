package fr.pickaria.menu

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MenuCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val menu: String = if (args.isNotEmpty()) {
				args[0]
			} else {
				DEFAULT_MENU
			}

			if (!menuController.openMenu(sender, menu, null)) {
				sender.sendMessage("§cCe menu n'existe pas.")
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
		if (sender is Player) {
			return menuController.menus.keys
				.filter { it.startsWith(args[0].lowercase()) }
				.toMutableList()
		}

		return mutableListOf()
	}
}