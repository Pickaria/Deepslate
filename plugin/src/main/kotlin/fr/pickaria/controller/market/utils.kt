package fr.pickaria.controller.market

import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.sendTo
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.SendResponse
import fr.pickaria.model.market.Order
import fr.pickaria.model.market.marketConfig
import fr.pickaria.shared.MiniMessage
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

data class OrderInfo(
	val orders: List<Pair<Order, Int>>,
	val totalAmount: Int,
	val totalPrice: Double,
)

private data class Notification(
	var boughtAmount: Int,
	var boughtPrice: Double,
) {
	operator fun plus(other: Notification): Notification =
		Notification(boughtAmount + other.boughtAmount, boughtPrice + other.boughtPrice)

	operator fun plusAssign(other: Notification) {
		boughtAmount += other.boughtAmount
		boughtPrice += other.boughtPrice
	}
}

fun getPrices(material: Material, amount: Int): OrderInfo {
	var remaining = amount

	// Order to Amount to buy
	val orders = Order.findSellOrders(material).mapNotNull {
		if (remaining > 0) {
			val value = it to max(min(remaining, it.amount), 0)
			remaining -= it.amount
			value
		} else {
			null
		}
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
	val notifications = mutableMapOf<OfflinePlayer, Notification>()

	info.orders.forEach { (order, buyingAmount) ->
		val price = order.price * buyingAmount

		when (sendTo(Credit, player, order.seller, price)) {
			SendResponse.SUCCESS -> {
				order.amount -= buyingAmount

				val newNotification = Notification(buyingAmount, price)

				notifications[order.seller]?.let {
					it += newNotification
				} ?: run {
					notifications[order.seller] = newNotification
				}

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

	MiniMessage("<gold><amount> <material><gray> acheté(s) pour <gold><price><gray>.") {
		"amount" to info.totalAmount
		"material" to Component.translatable(material.translationKey())
		"price" to Credit.economy.format(info.totalPrice)
	}.send(player)

	notifications.forEach { (seller, notification) ->
		if (seller.isOnline) {
			MiniMessage("<gold><player><gray> vous a acheté <gold><amount> <material><gray> pour <gold><price><gray>.") {
				"player" to player.displayName()
				"amount" to notification.boughtAmount
				"material" to Component.translatable(material.translationKey())
				"price" to Credit.economy.format(notification.boughtPrice)
			}.send(seller as Player)
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