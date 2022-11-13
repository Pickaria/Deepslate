package fr.pickaria.shard

import org.bukkit.Material
import org.bukkit.block.EnderChest
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

internal class PlaceShopCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			sender.location.block.let {
				it.type = Material.ENDER_CHEST
				val state = (it.state as EnderChest)
				state.persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
				state.update() // Extremely important!
			}

			// TODO: For testing purposes, remove once not needed
			sender.inventory.addItem(Shard.createItem())
		}

		return true
	}
}
