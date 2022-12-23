package fr.pickaria.vue.shop

import fr.pickaria.model.shop.ShopType
import fr.pickaria.model.shop.shopConfig
import fr.pickaria.model.shop.toController
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class CreateShops : CommandExecutor, TabCompleter {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			args.firstOrNull()?.let {
				shopConfig.villagers[args.first()]?.toController()?.create(sender.location)
			} ?: shopConfig.villagers.forEach {
				it.value.toController().create(sender.location)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender, command: Command, label: String, args: Array<out String>
	): MutableList<String> =
		ShopType.values().map { it.name.lowercase() }.filter { it.startsWith(args.first()) }.toMutableList()
}