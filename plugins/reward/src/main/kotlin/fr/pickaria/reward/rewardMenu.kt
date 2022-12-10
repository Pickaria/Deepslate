package fr.pickaria.reward

import fr.pickaria.economy.Key
import fr.pickaria.economy.Shard
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.deepslate.home.addToHome
import fr.pickaria.menu.menu
import net.kyori.adventure.text.Component
import org.bukkit.Material

internal fun rewardMenu() = menu("reward") {
	title = Component.text("Récompenses")
	rows = 3

	var x = 0

	Config.rewards
		.filter { it.value.purchasable }
		.forEach { (key, reward) ->
		item {
			slot = x++
			title = reward.name
			material = reward.material
			lore {
				keyValues {
					"Clés" to Key.economy.format(reward.keys.toDouble())
					"Éclats" to Shard.economy.format(reward.shards.toDouble())
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