package fr.pickaria.controller.market

import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.sendTo
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.SendResponse
import fr.pickaria.model.market.Order
import fr.pickaria.model.market.marketConfig
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.security.InvalidParameterException
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
internal fun buy(player: Player, material: Material, amount: Int): Int {
	val info = getPrices(material, amount)

	if (info.totalAmount != amount) {
		player.sendMessage(Component.text("Ce matériau n'est pas en stocks.", NamedTextColor.RED))
		return 0
	}

	if (!(player.has(Credit, info.totalPrice))) {
		player.sendMessage(Component.text("Vous n'avez pas assez d'argent.", NamedTextColor.RED))
		return 0
	}

	// Stores the data to notify sellers
	val notifications: MutableMap<OfflinePlayer, Pair<Int, Double>> = mutableMapOf()

	// FIXME: Possible item duplication
	info.orders.forEach { (order, buyingAmount) ->
		val price = order.price * buyingAmount

		when (sendTo(Credit, player, order.seller, price)) {
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
		.append(Component.text(Credit.economy.format(info.totalPrice), NamedTextColor.GOLD))
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
				.append(Component.text(Credit.economy.format(price), NamedTextColor.GOLD))
				.append(Component.text(".", NamedTextColor.GRAY))

			(seller as Player).sendMessage(message)
		}
	}

	return info.totalAmount
}

fun overflowStacks(material: Material, amountToGive: Int, meta: ((ItemMeta) -> Unit)? = null): List<ItemStack> {
	val stacks = mutableListOf<ItemStack>()
	if (material.maxStackSize == 0) {
		throw InvalidParameterException("Material has a maxStackSize of 0.")
	}
	var restToGive = amountToGive
	val item = ItemStack(material).apply {
		editMeta {
			meta?.invoke(it)
		}
	}

	while (restToGive > 0) {
		val amount = min(restToGive, material.maxStackSize)
		stacks += item.asQuantity(amount)
		restToGive -= material.maxStackSize
	}

	return stacks
}

@Deprecated(
	"", ReplaceWith(
		"player.give(*overflowStacks(material, amountToGive).toTypedArray())",
		"fr.pickaria.shared.give"
	)
)
internal fun giveItems(player: Player, material: Material, amountToGive: Int) {
	player.give(*overflowStacks(material, amountToGive).toTypedArray())
}

/**
 * Returns a pair containing the sell price and the buy price.
 */
internal fun getPrices(material: Material): Pair<Double, Double> {
	val average = Order.getAveragePrice(material)
	return max(average * marketConfig.sellPercentage, marketConfig.minimumPrice) to average * marketConfig.buyPercentage
}

internal fun getMenuItems(material: Material, stocks: Int): List<Pair<Int, Int>> {
	val maxStackSize = min(material.maxStackSize, stocks)

	return if (maxStackSize in 3 until stocks) {
		listOf(1 to 1, maxStackSize / 2 to 3, maxStackSize to 5, stocks to 7)
	} else if (maxStackSize == 2 && stocks < 2) {
		listOf(1 to 2, maxStackSize to 4, stocks to 6)
	} else if (stocks > 3) {
		listOf(1 to 2, stocks / 2 to 4, stocks to 6)
	} else if (stocks > 1) {
		listOf(1 to 2, stocks to 6)
	} else {
		listOf(1 to 4)
	}
}