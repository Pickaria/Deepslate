package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MoneyCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val balance = economy.format(economy.getBalance(sender))
			val placeholder = Placeholder.unparsed("balance", balance)
			val message = miniMessage.deserialize(economyConfig.balanceMessage, placeholder)
			sender.sendMessage(message)
			return true
		}

		return false
	}
}