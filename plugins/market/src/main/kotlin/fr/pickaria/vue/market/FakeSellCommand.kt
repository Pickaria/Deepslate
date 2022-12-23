package fr.pickaria.vue.market

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.random.Random


internal class FakeSellCommand : CommandExecutor {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			sender.sendMessage("Creating fake orders...")
			var amount = 0

			val materials = Material.values().toList().filter {
				try {
					it.creativeCategory != null
				} catch (_: Exception) {
					false
				}
			}

			val start = System.currentTimeMillis()

			for (material in materials) {
				val orders = Random.nextInt(1, 10)
				amount += orders
				for (i in 0..orders) {
					val quantity = Random.nextInt(1, 64)
					val rarity = material.itemRarity.ordinal + 1
					val price = Random.nextDouble(1.0, 100.0) * rarity
					Order.create(sender, material, OrderType.SELL, quantity, price)
				}
			}

			val end = System.currentTimeMillis()

			sender.sendMessage("Created $amount orders in ${end - start} ms")
		}

		return true
	}
}