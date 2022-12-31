package fr.pickaria.vue.premium

import fr.pickaria.controller.home.addToHome
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import fr.pickaria.model.premium.premiumConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

fun premiumMenu() = menu("premium") {
	title = Component.text("Grades premium", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
	rows = 4

	val count = premiumConfig.ranks.size
	var x = 4 - count + 1

	premiumConfig.ranks.forEach { (key, rank) ->
		item {
			position = x to 1
			title = rank.name.decorate(TextDecoration.BOLD)
			material = rank.material
			leftClick = Result.CLOSE to "/premium buy $key"
			lore {
				description {
					rank.description.forEach {
						-MiniMessage(it).message.decoration(
							TextDecoration.ITALIC,
							TextDecoration.State.FALSE
						)
					}
				}

				keyValues {
					"Prix" to Shard.toController().format(rank.price.toDouble())
					"Durée" to "${rank.duration} secondes"
				}
			}
		}

		x += 2
	}

	fill(Material.MAGENTA_STAINED_GLASS_PANE, true)
	closeItem()
}.addToHome(Material.DIAMOND, Component.text("Grades")) {
	description {
		-"Acheter des grades premium."
	}
}