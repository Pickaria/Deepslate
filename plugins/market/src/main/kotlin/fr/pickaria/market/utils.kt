package fr.pickaria.market

import fr.pickaria.shared.models.Order
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import kotlin.math.min

/**
 * Finds sell orders and buys the specified amount of material.
 */
internal fun buy(player: Player, material: Material, maximumPrice: Double, amount: Int) {
	var boughtAmount = 0
	var totalSpent = 0.0
	var i = 0

	val orders = Order.findSellOrders(material, maximumPrice)

	for (order in orders) {
		val buyingAmount = min(amount - boughtAmount, order.amount)

		val price = buyingAmount * order.price
		if (!economy.has(player, price)) {
			break
		}

		boughtAmount += buyingAmount
		order.amount -= buyingAmount
		totalSpent += price

		economy.withdrawPlayer(player, price)
		economy.depositPlayer(order.seller, price)

		if (boughtAmount >= amount) {
			break
		}
	}

	val remainingToBuy = amount - boughtAmount

	val message = Component.text("$boughtAmount ", NamedTextColor.GOLD)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.append(Component.text(" acheté(s) pour ", NamedTextColor.GRAY))
		.append(Component.text(economy.format(totalSpent), NamedTextColor.GOLD))
		.append(Component.text(".", NamedTextColor.GRAY))

	player.sendMessage(message)

	if (remainingToBuy > 0) {
		val message = Component.text("$remainingToBuy", NamedTextColor.GOLD)
			.append(Component.text(" n'ont pas pu être achetés au prix maximum de ", NamedTextColor.GRAY))
			.append(Component.text(economy.format(maximumPrice), NamedTextColor.GOLD))
			.append(Component.text(".", NamedTextColor.GRAY))

		player.sendMessage(message)
	}

}