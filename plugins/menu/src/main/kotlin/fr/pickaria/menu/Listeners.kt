package fr.pickaria.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
					result = Event.Result.DENY
					isCancelled = true

					try {
						holder.menu(this)
					} catch (e: Exception) {
						e.printStackTrace()
						whoClicked.closeInventory()
						whoClicked.sendMessage(Component.text("Une erreur critique est survenue.", NamedTextColor.RED))
					}
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