package fr.pickaria.market.menu

import fr.pickaria.database.models.Order
import fr.pickaria.database.models.OrderType
import fr.pickaria.economy.Credit
import fr.pickaria.economy.has
import fr.pickaria.market.getPrices
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import kotlin.math.min

internal fun buyMenu(material: Material) = menu {
	title = Component.text("Acheter ", NamedTextColor.GRAY)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.decorate(TextDecoration.BOLD)
	rows = 4

	val unitPrice = Credit.economy.format(Order.getPrices(material).first)
	val stocks = Order.getSumAmount(OrderType.SELL, material)
	val maxStackSize = min(material.maxStackSize, stocks)

	val items = if (maxStackSize in 3 until stocks) {
		listOf(1 to 1, maxStackSize / 2 to 3, maxStackSize to 5, stocks to 7)
	} else if (maxStackSize == 2 && stocks < 2) {
		listOf(1 to 2, maxStackSize to 4, stocks to 6)
	} else if (stocks > 3) {
		listOf(1 to 2, stocks / 2 to 4, stocks to 6)
	} else if (stocks == 1) {
		listOf(1 to 4)
	} else {
		listOf(1 to 2, stocks to 6)
	}

	items.forEach { (amount, x) ->
		val info = getPrices(material, amount)
		val player = opener as OfflinePlayer

		item {
			position = x to 1
			title = Component.text("$amount ")
				.append(Component.translatable(material.translationKey()))

			if (player.has(Credit, info.totalPrice) && amount <= info.totalAmount) {
				this.material = material

				lore {
					leftClick = "Clic-gauche pour acheter $amount"
					keyValues {
						"Prix unitaire à partir de" to unitPrice
						"Prix total" to Credit.economy.format(info.totalPrice)
					}
				}

				leftClick = Result.REFRESH to "/buy ${material.name.lowercase()} $amount"
			} else if (amount <= info.totalAmount) {
				this.material = Material.BARRIER

				lore {
					description {
						-"Vous n'avez pas assez"
						-"d'argent pour acheter ceci."
					}
					keyValues {
						"Prix total" to Credit.economy.format(info.totalPrice)
					}
				}
			} else {
				this.material = Material.BARRIER

				lore {
					description {
						-"Cet article n'est plus"
						-"disponible à la vente."
					}
				}
			}
		}
	}

	fill(Material.ORANGE_STAINED_GLASS_PANE, true)
	closeItem()
}