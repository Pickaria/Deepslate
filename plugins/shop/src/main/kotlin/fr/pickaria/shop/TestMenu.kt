package fr.pickaria.shop

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType

class TestMenu: CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			val inventory = Bukkit.createInventory(sender, InventoryType.MERCHANT)
			sender.openInventory(inventory)
			inventory.clear()
		}

		return true
	}
}
