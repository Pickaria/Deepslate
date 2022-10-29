package fr.pickaria.market

import fr.pickaria.economy.SendResponse
import fr.pickaria.economy.sendTo
import fr.pickaria.shared.models.Order
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.min

/**
 * Finds sell orders and buys the specified amount of material.
 */
internal fun buy(player: Player, material: Material, maximumPrice: Double, amount: Int): Int {
	var boughtAmount = 0
	var totalSpent = 0.0
	var i = 0
	var notEnoughMoney = false

	val orders = Order.findSellOrders(material, maximumPrice)

	for (order in orders) {
		val buyingAmount = min(amount - boughtAmount, order.amount)

		val price = buyingAmount * order.price

		when (sendTo(player, order.seller, price)) {
			SendResponse.SUCCESS -> {
				boughtAmount += buyingAmount
				order.amount -= buyingAmount
				totalSpent += price

				if (order.seller.isOnline) {
					val seller = order.seller as Player

					val message = player.displayName().color(NamedTextColor.GOLD)
						.append(Component.text(" vous a acheté ", NamedTextColor.GRAY))
						.append(
							Component.text("$buyingAmount ")
								.append(Component.translatable(material.translationKey()))
								.color(NamedTextColor.GOLD)
						)
						.append(Component.text(" pour ", NamedTextColor.GRAY))
						.append(Component.text(economy.format(price), NamedTextColor.GOLD))
						.append(Component.text(".", NamedTextColor.GRAY))

					seller.sendMessage(message)
				}

				notEnoughMoney = false
			}

			SendResponse.NOT_ENOUGH_MONEY -> {
				notEnoughMoney = true
			}

			else -> {}
		}

		if (boughtAmount >= amount) {
			break
		}
	}

	if (notEnoughMoney) {
		val message = Component.text("Vous n'avez pas assez d'argent.", NamedTextColor.RED)
		player.sendMessage(message)
	}

	val remainingToBuy = amount - boughtAmount

	if (boughtAmount > 0) {
		val message = Component.text("$boughtAmount ", NamedTextColor.GOLD)
			.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
			.append(Component.text(" acheté(s) pour ", NamedTextColor.GRAY))
			.append(Component.text(economy.format(totalSpent), NamedTextColor.GOLD))
			.append(Component.text(".", NamedTextColor.GRAY))

		player.sendMessage(message)
	}

	if (remainingToBuy > 0) {
		val message = Component.text("$remainingToBuy", NamedTextColor.GOLD)
			.append(Component.text(" n'ont pas pu être achetés au prix maximum de ", NamedTextColor.GRAY))
			.append(Component.text(economy.format(maximumPrice), NamedTextColor.GOLD))
			.append(Component.text(".", NamedTextColor.GRAY))

		player.sendMessage(message)
	}

	return boughtAmount
}