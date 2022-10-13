package fr.pickaria.economy

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MoneyCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val balance = economy.format(economy.getBalance(sender))
			sender.sendMessage("§7Votre solde : §6$balance")
			return true
		}

		return false
	}
}