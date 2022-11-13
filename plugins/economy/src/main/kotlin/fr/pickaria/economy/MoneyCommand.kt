package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class MoneyCommand : CommandExecutor, CurrencyExtensions(Credit) {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val balance = economy.format(sender.balance)
			val placeholder = Placeholder.unparsed("balance", balance)
			val message = miniMessage.deserialize(Config.balanceMessage, placeholder)
			sender.sendMessage(message)

			// TODO: Remove next line
			sender.inventory.addItem(Credit.createItem())
		}

		return true
	}
}