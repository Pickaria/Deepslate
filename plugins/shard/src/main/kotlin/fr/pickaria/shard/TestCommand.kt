package fr.pickaria.shard

import fr.pickaria.shared.models.ShopItem
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

internal class TestCommand: CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val (bytes, amount) = sender.inventory.itemInMainHand.let {
				it.asOne().serializeAsBytes() to it.amount
			}

			sender.sendMessage("Item is ${bytes.size} bytes long")

			ShopItem.get(bytes)?.let {
				sender.sendMessage("Updated amount")
				it.amount += amount
			} ?: run {
				sender.sendMessage("Created item")
				ShopItem.create(bytes, amount)
			}
		}

		return true
	}
}
