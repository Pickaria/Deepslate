package fr.pickaria.market.menu

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.market.economy
import fr.pickaria.market.getPrices
import fr.pickaria.menu.*
import fr.pickaria.menu.home.addToHome
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

@OptIn(ItemBuilderUnsafe::class)
internal fun orderListingMenu() = menu("market") {
	title = Component.text("Marché", NamedTextColor.GOLD, TextDecoration.BOLD)

	val count = Order.count(OrderType.SELL)
	val pageSize = size - 9
	val start = page * pageSize
	val orders = Order.getListings(OrderType.SELL, pageSize, start.toLong())
	val maxPage = (count - 1) / pageSize

	orders.forEachIndexed { index, order ->
		val (sellPrice, buyPrice) = getPrices(order.averagePrice)

		item {
			slot = index
			material = order.material
			title = Component.translatable(order.material.translationKey())
			lore {
				keyValues {
					"Quantité en vente" to order.amount
					"Capitalisation boursière" to economy.format(order.amount * order.averagePrice)
					"Achat immédiat" to economy.format(buyPrice)
					"Vente immédiate" to economy.format(sellPrice)
					"Prix moyen" to economy.format(order.averagePrice)
				}
				leftClick = "Clic-gauche pour voir les options d'achat"
				rightClick = "Clic-droit pour voir les options de vente"
			}
			leftClick {
				opener open buyMenu(order.material)
			}
			rightClick {
				opener open sellMenu(order.material)
			}
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage.toInt())
}.addToHome(Material.EMERALD, Component.text("Marché", NamedTextColor.GOLD)) {
	description {
		-"Achetez et vendez toute sorte de matériaux."
	}
}