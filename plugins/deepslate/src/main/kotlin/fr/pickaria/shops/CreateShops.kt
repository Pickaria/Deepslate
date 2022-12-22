package fr.pickaria.shops

import fr.pickaria.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CreateShops : CommandExecutor, TabCompleter {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			args.firstOrNull()?.let {
				Config.villagers[args.first()]?.create(sender.location)
			} ?: Config.villagers.forEach {
				it.value.create(sender.location)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender, command: Command, label: String, args: Array<out String>
	): MutableList<String> =
		ShopType.values().map { it.name.lowercase() }.filter { it.startsWith(args.first()) }.toMutableList()
}