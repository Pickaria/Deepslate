package fr.pickaria.vue.potion

import fr.pickaria.controller.potion.addPotionEffect
import fr.pickaria.model.potion.potionConfig
import fr.pickaria.model.potion.potionNamespace
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

internal class PotionListener : Listener {
	@EventHandler
	fun onPlayerConsumeEvent(event: PlayerItemConsumeEvent) {
		with(event) {
			if (item.type == Material.POTION) {
				val potion = (item.itemMeta as PotionMeta)

				potion.persistentDataContainer.get(potionNamespace, PersistentDataType.STRING)?.let {
					potionConfig.potions[it.lowercase()]?.let { config ->
						addPotionEffect(config, player)
						if (player.gameMode !== GameMode.CREATIVE) {
							setItem(null)
						}
					}
				}
			}
		}
	}
}