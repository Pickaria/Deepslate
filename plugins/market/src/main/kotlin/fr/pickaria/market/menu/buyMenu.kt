package fr.pickaria.market.menu

import fr.pickaria.economy.has
import fr.pickaria.market.economy
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

internal fun buyMenu(material: Material) = menu {
	title = Component.text("Vendre ", NamedTextColor.GRAY)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.decorate(TextDecoration.BOLD)
	rows = 4

	val price = getPrices(material).second
	val unitPrice = economy.format(price)

	listOf(1 to 1, 16 to 3, 32 to 5, 64 to 7).forEach { (amount, x) ->
		val offerPrice = economy.format(price * amount)
		val player = opener as OfflinePlayer

		item {
			position = x to 1
			title = Component.text("$amount ")
				.append(Component.translatable(material.translationKey()))

			if (player has price * amount) {
				this.material = material

				lore {
					leftClick = "Clic-gauche pour acheter $amount"
					keyValues {
						"Prix unitaire" to unitPrice
						"Prix total" to offerPrice
					}
				}

				leftClick = "/buy ${material.name.lowercase()} $amount $price"
			} else {
				this.material = Material.BARRIER

				lore {
					description {
						-"Vous n'avez pas assez"
						-"d'argent pour acheter ceci."
					}
				}
			}
		}
	}

	fill(Material.ORANGE_STAINED_GLASS_PANE, true)
	closeItem()
}