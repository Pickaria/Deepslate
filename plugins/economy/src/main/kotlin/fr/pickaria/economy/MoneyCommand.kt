package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class MoneyCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val balance = economy.format(economy.getBalance(sender))
			val placeholder = Placeholder.unparsed("balance", balance)
			val message = miniMessage.deserialize(Config.balanceMessage, placeholder)
			sender.sendMessage(message)

			sender.inventory.addItem(createCoinItem(8.0, 32))
			sender.inventory.addItem(ItemStack(Material.VOID_AIR))
			return true
		}

		return false
	}
}