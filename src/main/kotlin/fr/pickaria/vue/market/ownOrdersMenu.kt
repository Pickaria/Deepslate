package fr.pickaria.vue.market

import fr.pickaria.controller.home.addToHome
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.market.Order
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal fun ownOrdersMenu() = menu("orders") {
	val count = Order.count(opener)

	title = MiniMessage("<gold><b>Mes ventes</b> <gray>(<amount>)") {
		"amount" to count
	}.toComponent()

	val pageSize = size - 9
	val start = page * pageSize
	val orders = Order.get(opener, pageSize, start.toLong())
	val maxPage = (count - 1) / pageSize

	orders.forEachIndexed { index, order ->
		item {
			slot = index
			material = order.material
			title = Component.translatable(order.material.translationKey())
			lore {
				keyValues {
					"Identifiant" to order.id
					"Quantité en vente" to order.amount
					"Prix de vente" to Credit.economy.format(order.price)
				}
				leftClick = "Clic-gauche pour retirer de la vente"
			}
			leftClick = Result.REFRESH to "/cancel ${order.id}"
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage.toInt())
}.addToHome(Material.PAPER, Component.text("Mes ventes")) {
	description {
		-"Gérer ses ventes au marché."
	}
}