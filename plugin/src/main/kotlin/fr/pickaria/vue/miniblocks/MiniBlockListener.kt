package fr.pickaria.vue.miniblocks

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.meta.SkullMeta

class MiniBlockListener : Listener {
	@EventHandler
	fun onBlockDropMiniBlock(event: BlockDropItemEvent) {
		with(event) {
			items.forEach { item ->
				val type = item.itemStack.type
				val meta = item.itemStack.itemMeta
				if ((type == Material.PLAYER_HEAD || type == Material.PLAYER_WALL_HEAD) && meta is SkullMeta) {
					meta.playerProfile?.properties?.find { it.name == "material" }?.let { translation ->
						items.firstOrNull()?.let {
							it.itemStack.editMeta { meta ->
								meta.displayName(
									Component.translatable(translation.value)
										.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
								)
							}
						}
					}
				}
			}
		}
	}
}