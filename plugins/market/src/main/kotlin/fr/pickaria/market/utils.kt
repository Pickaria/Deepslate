package fr.pickaria.market

import fr.pickaria.database.models.Order
import fr.pickaria.economy.SendResponse
import fr.pickaria.economy.send
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.min

/**
 * Finds sell orders and buys the specified amount of material.
 */
internal fun buy(player: Player, material: Material, maximumPrice: Double, amount: Int): Int {
	// Total amount of material that has been bought
	var boughtAmount = 0

	// Total amount of money spent to acquire the given material
	var totalSpent = 0.0

	val orders = Order.findSellOrders(material, maximumPrice)

	// Stores the data to notify sellers
	val notifications: MutableMap<OfflinePlayer, Pair<Int, Double>> = mutableMapOf()

	// Maximum amount of material the player can buy with their balance
	val maximumCanAfford = (economy.getBalance(player) / maximumPrice).toInt()

	// Whether the player ended up not having enough money
	var notEnoughMoney = maximumCanAfford == 0

	for (order in orders) {
		// How much material can be bought from this order
		val buyingAmount = minOf(amount - boughtAmount, order.amount, maximumCanAfford)

		if (buyingAmount == 0) {
			continue
		}

		// Total amount of money to spend
		val price = buyingAmount * order.price

		when (player send price to order.seller) {
			SendResponse.SUCCESS -> {
				boughtAmount += buyingAmount
				order.amount -= buyingAmount
				totalSpent += price

				val notification = notifications.getOrDefault(order.seller, 0 to 0.0)
				notifications[order.seller] = notification.first + buyingAmount to notification.second + price

				// Update order if it is empty
				if (order.amount - buyingAmount <= 0) {
					order.delete()
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

	if (notEnoughMoney && boughtAmount < amount) {
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

	notifications.forEach { (seller, pair) ->
		val (buyingAmount, price) = pair

		if (seller.isOnline) {
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

			(seller as Player).sendMessage(message)
		}
	}

	return boughtAmount
}

// TODO: Create `sell` function, any item bought will be placed in (#31)

// TODO: Give to OfflinePlayer through their dematerialized inventory (#31)
internal fun giveItems(player: Player, material: Material, amountToGive: Int) {
	var restToGive = amountToGive
	val item = ItemStack(material)

	while (restToGive > 0) {
		val amount = min(restToGive, material.maxStackSize)
		player.inventory.addItem(item.asQuantity(amount)).forEach { (_, it) ->
			// Drop items that cannot be added
			player.world.dropItem(player.location, it)
		}
		restToGive -= material.maxStackSize
	}
}

/**
 * Returns a pair containing the sell price and the buy price.
 */
internal fun getPrices(material: Material): Pair<Double, Double> {
	val average = Order.getAveragePrice(material)
	return min(average * Config.sellPercentage, Config.minimumPrice) to average * Config.buyPercentage
}