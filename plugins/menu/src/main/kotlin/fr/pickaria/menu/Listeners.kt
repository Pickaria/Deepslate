package fr.pickaria.menu

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

internal class Listeners : Listener {
	@EventHandler
	fun onMenuClick(event: InventoryClickEvent) {
		with(event) {
			inventory.holder?.let { holder ->
				if (holder is Holder) {

					holder.menu(this)

					event.result = Event.Result.DENY
					isCancelled = true
				}
			}
		}
	}

	@EventHandler
	private fun onInventoryDrag(event: InventoryDragEvent) {
		if (event.inventory.holder is Holder) {
			event.result = Event.Result.DENY
			event.isCancelled = true
		}
	}
}