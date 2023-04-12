package fr.pickaria.vue.town

import fr.pickaria.controller.town.isTownBook
import fr.pickaria.controller.town.openTownBook
import org.bukkit.Material
import org.bukkit.block.Lectern
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.LecternInventory
import org.bukkit.block.data.type.Lectern as LecternData

class BookListeners : Listener {
	@EventHandler
	fun onPlayerOpenBook(event: PlayerInteractEvent) {
		with(event) {
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				val isLectern = clickedBlock?.type == Material.LECTERN

				val bookItem = if (isLectern) {
					clickedBlock?.let {
						if (it.type == Material.LECTERN && (it.blockData as LecternData).hasBook()) {
							((it.state as Lectern).inventory as LecternInventory).book
						} else {
							null
						}
					}
				} else {
					item?.let {
						if (it.type == Material.WRITTEN_BOOK) {
							it
						} else {
							null
						}
					}
				}

				bookItem?.let {
					if (it.isTownBook()) {
						setUseInteractedBlock(Event.Result.DENY)
						setUseItemInHand(Event.Result.DENY)

						player.openTownBook()
					}
				}
			}
		}
	}
}