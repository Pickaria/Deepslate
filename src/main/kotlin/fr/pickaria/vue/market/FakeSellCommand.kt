package fr.pickaria.vue.market

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import fr.pickaria.model.market.Order
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.random.Random


@CommandAlias("fakesell")
@CommandPermission("pickaria.developer.command.fake")
@Description("Met en vente tous les matériaux du jeu.")
class FakeSellCommand : BaseCommand() {
	@Default
	fun onDefault(sender: Player) {
		sender.sendMessage("Creating fake orders...")
		var amount = 0

		val materials = Material.values().toList().filter {
			(it.isBlock || it.isItem) && !it.isEmpty
		}

		val start = System.currentTimeMillis()

		for (material in materials) {
			val orders = Random.nextInt(1, 10)
			amount += orders
			for (i in 0..orders) {
				val quantity = Random.nextInt(1, 64)
				val rarity = try {
					material.itemRarity.ordinal + 1
				} catch (_: IllegalArgumentException) {
					0
				}
				val price = Random.nextDouble(1.0, 100.0) * rarity
				Order.create(sender, material, quantity, price)
			}
		}

		val end = System.currentTimeMillis()

		sender.sendMessage("Created $amount orders in ${end - start} ms")
	}
}