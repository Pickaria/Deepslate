package fr.pickaria.reward

import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.home.addToHome
import fr.pickaria.menu.menu
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal fun rewardMenu() = menu("reward") {
	title = Component.text("Récompenses")
	rows = 3

	var x = 0

	Config.rewards.forEach { (key, reward) ->
		item {
			slot = x++
			title = reward.name
			material = reward.material
			lore {
				keyValues  {
					"Prix" to economy.format(reward.price)
				}
			}
			leftClick = Result.NONE to "/reward $key"
		}
	}

	closeItem()
}.addToHome(Material.SHULKER_BOX, Component.text("Récompenses")) {
	description {
		-"Achetez des caisses de récompenses."
	}
}