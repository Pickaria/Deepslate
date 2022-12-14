package fr.pickaria.vue.market

import fr.pickaria.controller.market.getMenuItems
import fr.pickaria.controller.market.getPrices
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Credit
import fr.pickaria.shared.sum
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal fun sellMenu(material: Material) = menu {
	title = Component.text("Vendre ", NamedTextColor.GRAY)
		.append(Component.translatable(material.translationKey(), NamedTextColor.GOLD))
		.decorate(TextDecoration.BOLD)
	rows = 4

	val price = getPrices(material).second
	val unitPrice = Credit.economy.format(price)
	val stocks = opener.inventory.sum(material)

	getMenuItems(material, stocks).forEach { (amount, x) ->
		val offerPrice = Credit.economy.format(price * amount)

		item {
			position = x to 1
			title = Component.text("$amount ")
				.append(Component.translatable(material.translationKey()))

			val item = ItemStack(material)
			val hasStocks = opener.inventory.containsAtLeast(item, amount)

			if (hasStocks) {
				this.material = material

				lore {
					leftClick = "Clic-gauche pour vendre $amount"
					keyValues {
						"Prix unitaire" to unitPrice
						"Prix total" to offerPrice
					}
				}

				leftClick = Result.REFRESH to "/sell ${material.name.lowercase()} $amount $price"
			} else {
				this.material = Material.BARRIER

				lore {
					description {
						-"Vous n'avez pas assez"
						-"dans votre inventaire."
					}
				}
			}
		}
	}

	fill(Material.ORANGE_STAINED_GLASS_PANE, true)
	closeItem()
}