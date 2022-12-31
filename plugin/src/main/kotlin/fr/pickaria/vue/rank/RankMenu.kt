package fr.pickaria.vue.rank

import fr.pickaria.controller.home.addToHome
import fr.pickaria.controller.libraries.luckperms.displayName
import fr.pickaria.controller.libraries.luckperms.getGroup
import fr.pickaria.controller.libraries.luckperms.luckPermsUser
import fr.pickaria.controller.rank.calculateRankUpgradePrice
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import fr.pickaria.model.rank.rankConfig
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material


fun rankMenu() = menu("ranks") {
	val user = opener.luckPermsUser
	val group = getGroup(user.primaryGroup)?.displayName() ?: Component.empty()

	title = Component.text("Grades premium", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
		.appendSpace()
		.append(group)
	rows = 4

	val count = rankConfig.ranks.size
	var x = 4 - count + 1

	rankConfig.ranks.forEach { (key, rank) ->
		val owns = opener.hasPermission(rank.permission)

		item {
			position = x to 1
			title = rank.name.decorate(TextDecoration.BOLD)
			material = rank.material
			leftClick = Result.CLOSE to "/ranks buy $key"

			lore {
				description {
					-if (owns) {
						rankConfig.ownedMessage
					} else {
						rankConfig.notOwnedMessage
					}.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)

					-""

					rank.description.forEach {
						-MiniMessage(it).message.decoration(
							TextDecoration.ITALIC,
							TextDecoration.State.FALSE
						)
					}
				}

				keyValues {
					"Prix" to Shard.toController().format(opener.calculateRankUpgradePrice(rank))
					"Durée" to "${rank.duration} secondes"
				}
			}

			if (owns) {
				editMeta {
					it.addEnchant(GlowEnchantment.instance, 1, true)
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