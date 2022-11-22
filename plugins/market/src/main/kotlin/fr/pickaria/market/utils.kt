package fr.pickaria.market

import fr.pickaria.database.models.Order
import fr.pickaria.economy.GlobalCurrencyExtensions
import fr.pickaria.economy.SendResponse
import fr.pickaria.economy.has
import fr.pickaria.economy.sendTo
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

internal data class OrderInfo(
	val orders: List<Pair<Order, Int>>,
	val totalAmount: Int,
	val totalPrice: Double,
)

internal fun getPrices(material: Material, amount: Int): OrderInfo {
	var remaining = amount

	// Order to Amount to buy
	val orders = Order.findSellOrders(material).map {
		val value = it to max(min(remaining, it.amount), 0)
		remaining -= it.amount
		value
	}

	val totalAmount = orders.sumOf { it.second }
	val totalPrice = orders.sumOf { (order, amount) -> order.price * amount }

	return OrderInfo(orders, totalAmount, totalPrice)
}

/**
 * Finds sell orders and buys the specified amount of material.
 */
@OptIn(GlobalCurrencyExtensions::class)
internal fun buy(player: Player, material: Material, amount: Int): Int {
	val info = getPrices(material, amount)

	if (info.totalAmount != amount) {
		player.sendMessage(Component.text("Ce matériau n'est pas en stocks.", NamedTextColor.RED))
		return 0
	}

	if (!(player has info.totalPrice)) {
		player.sendMessage(Component.text("Vous n'avez pas assez d'argent.", NamedTextColor.RED))
		return 0
	}

	// Stores the data to notify sellers
	val notifications: MutableMap<OfflinePlayer, Pair<Int, Double>> = mutableMapOf()

	info.orders.forEach { (order, buyingAmount) ->
		val price = order.price * buyingAmount

		when (sendTo(player, order.seller, price)) {
			SendResponse.SUCCESS -> {
				order.amount -= buyingAmount

				val notification = notifications.getOrDefault(order.seller, 0 to 0.0)
				notifications[order.seller] = notification.first + buyingAmount to notification.second + price

				// Update order if it is empty
				if (order.amount - buyingAmount <= 0) {
					order.delete()
				}
			}

			SendResponse.NOT_ENOUGH_MONEY -> {
				player.sendMessage(Component.text("Vous n'avez pas assez d'argent.", NamedTextColor.RED))
			}

			else -> {
				player.sendMessage(Component.text("Une erreur lors de l'achat est survenue.", NamedTextColor.RED))
			}
		}
	}

	val message = Component.text("${info.totalAmount} ", NamedTextColor.GOLD)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.append(Component.text(" acheté(s) pour ", NamedTextColor.GRAY))
		.append(Component.text(economy.format(info.totalPrice), NamedTextColor.GOLD))
		.append(Component.text(".", NamedTextColor.GRAY))

	player.sendMessage(message)

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

	return info.totalAmount
}

// TODO: Create `sell` function, any item bought will be placed in dematerialized inventory (#31)

// TODO: Give to OfflinePlayer through their dematerialized inventory (#31)
internal fun giveItems(player: Player, material: Material, amountToGive: Int) {
	var restToGive = amountToGive
	val item = ItemStack(material)

	while (restToGive > 0) {
		val amount = min(restToGive, material.maxStackSize)
		player.give(item.asQuantity(amount))
		restToGive -= material.maxStackSize
	}
}

/**
 * Returns a pair containing the sell price and the buy price.
 */
internal fun getPrices(material: Material): Pair<Double, Double> {
	val average = Order.getAveragePrice(material)
	return max(average * Config.sellPercentage, Config.minimumPrice) to average * Config.buyPercentage
}

/**
 * Returns a pair containing the sell price and the buy price.
 */
internal fun getPrices(average: Double): Pair<Double, Double> =
	max(average * Config.sellPercentage, Config.minimumPrice) to average * Config.buyPercentage
