package fr.pickaria.vue

import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class SignEdit : Listener {
	@EventHandler
	fun onPlayerClickOnSign(event: PlayerInteractEvent) {
		with(event) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				clickedBlock?.let {
					if (it.state is Sign) {
						player.openSign(it.state as Sign)
					}
				}
			}
		}
	}
}