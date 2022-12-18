package fr.pickaria.market.menu

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.market.economy
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.home.addToHome
import fr.pickaria.menu.menu
import fr.pickaria.menu.nextPage
import fr.pickaria.menu.previousPage
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal fun ownOrdersMenu() = menu("orders") {
	title = Component.text("Mes ordres")

	val count = Order.count(OrderType.SELL, opener)
	val pageSize = size - 9
	val start = page * pageSize
	val orders = Order.get(opener, pageSize, start.toLong())
	val maxPage = (count - 1) / pageSize

	orders.forEachIndexed { index, order ->
		val orderType = if (order.type == OrderType.SELL) {
			"vendre"
		} else {
			"achat"
		}

		item {
			slot = index
			material = order.material
			title = Component.translatable(order.material.translationKey())
			lore {
				keyValues {
					"Identifiant" to order.id
					"Type d'ordre" to orderType
					"Quantité en vente" to order.amount
					"Prix de vente" to economy.format(order.price)
				}
				leftClick = "Clic-gauche pour retirer de la vente"
			}
			leftClick = Result.REFRESH to "/cancel ${order.id}"
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage.toInt())
}.addToHome(Material.PAPER, Component.text("Mes ordres")) {
	description {
		-"Gérer ses ordres de vente et d'achat."
	}
}