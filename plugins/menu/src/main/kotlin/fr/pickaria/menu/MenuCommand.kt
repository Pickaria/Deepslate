package fr.pickaria.menu

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class MenuCommand : CommandExecutor, TabCompleter {
	companion object {
		val DEFAULT_MENU = MenuEnum.HOME
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val menuEnum: MenuEnum = if (args.isNotEmpty()) {
				try {
					MenuEnum.valueOf(args[0].uppercase())
				} catch (_: IllegalArgumentException) {
					sender.sendMessage("§cCe menu n'existe pas.")
					return true
				}
			} else {
				DEFAULT_MENU
			}

			if (!Main.menuController.openMenu(sender, menuEnum, null)) {
				sender.sendMessage("§cUne erreur est survenue lors de l'ouverture du menu.")
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
			return MenuEnum.values()
				.map { it.name.lowercase() }
				.filter { it.startsWith(args[0].lowercase()) }
				.toMutableList()
		}

		return mutableListOf()
	}
}