package fr.pickaria.vue.market

import fr.pickaria.controller.economy.has
import fr.pickaria.controller.market.getMenuItems
import fr.pickaria.controller.market.getPrices
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.market.Order
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.OfflinePlayer

internal fun buyMenu(material: Material) = menu {
	title = Component.text("Acheter ", NamedTextColor.GRAY)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.decorate(TextDecoration.BOLD)
	rows = 4

	val unitPrice = Credit.economy.format(Order.getPrices(material).first)
	val stocks = Order.getSumAmount(material)

	getMenuItems(material, stocks).forEach { (amount, x) ->
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