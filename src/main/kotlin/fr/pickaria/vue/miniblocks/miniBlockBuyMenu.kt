package fr.pickaria.vue.miniblocks

import fr.pickaria.controller.economy.has
import fr.pickaria.controller.market.getMenuItems
import fr.pickaria.controller.toController
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.toController
import fr.pickaria.model.miniblocks.MiniBlock
import fr.pickaria.model.miniblocks.miniBlocksConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

@OptIn(ItemBuilderUnsafe::class)
internal fun miniBlockBuyMenu(miniBlock: MiniBlock) = menu {
	title = Component.text("Acheter ", NamedTextColor.GRAY)
		.append(Component.translatable(miniBlock.material.translationKey(), NamedTextColor.GOLD))
		.decorate(TextDecoration.BOLD)
	rows = 4

	val unitPrice = miniBlock.price ?: miniBlocksConfig.defaultPrice
	val formattedUnitPrice = Credit.toController().format(unitPrice)

	getMenuItems(Material.PLAYER_HEAD, 64).forEach { (amount, x) ->
		val stack = miniBlock.toController().create(amount)
		val totalPrice = unitPrice * amount

		item {
			position = x to 1
			title = Component.text("$amount ")
				.append(Component.translatable(miniBlock.material.translationKey()))

			if (opener.has(Credit, totalPrice)) {
				material = Material.PLAYER_HEAD
				setMeta(stack.itemMeta)

				lore {
					leftClick = "Clic-gauche pour acheter $amount"
					keyValues {
						"Prix unitaire" to formattedUnitPrice
						"Prix total" to Credit.toController().format(totalPrice)
					}
				}

				leftClick = Result.REFRESH to "/miniblock buy ${miniBlock.material.name.lowercase()} $amount"
			} else {
				material = Material.BARRIER

				lore {
					description {
						-"Vous n'avez pas assez"
						-"d'argent pour acheter ceci."
					}
					keyValues {
						"Prix total" to Credit.toController().format(totalPrice)
					}
				}
			}
		}
	}

	fill(Material.ORANGE_STAINED_GLASS_PANE, true)
	closeItem()
}