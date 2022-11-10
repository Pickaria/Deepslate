package fr.pickaria.menu

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

internal class Command : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val menu: String = if (args.isNotEmpty()) {
				args[0]
			} else {
				DEFAULT_MENU
			}

			val page = try {
				args.getOrNull(1)?.toInt()
			} catch (_: NumberFormatException) {
				null
			}

			try {
				val opened = if (page != null) {
					sender.open(menu, page)
				} else {
					sender open menu
				}

				if (!opened) {
					sender.sendMessage("§cCe menu n'existe pas.")
				}
			} catch (exception: Exception) {
				sender.sendMessage("§cUne erreur est survenue lors de l'ouverture du menu.")
				exception.printStackTrace()
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String> = builders.keys
		.filter { it.startsWith(args[0].lowercase()) }
		.toMutableList()
}
