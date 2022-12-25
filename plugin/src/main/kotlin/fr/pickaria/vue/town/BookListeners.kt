package fr.pickaria.vue.town

import fr.pickaria.controller.town.TownController
import fr.pickaria.controller.town.isTownBook
import fr.pickaria.controller.town.openTownBook
import fr.pickaria.controller.town.townId
import org.bukkit.Material
import org.bukkit.block.Lectern
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerTakeLecternBookEvent
import org.bukkit.inventory.LecternInventory
import org.bukkit.block.data.type.Lectern as LecternData

class BookListeners : Listener {
	@EventHandler
	fun onPlayerTakeLecternBook(event: PlayerTakeLecternBookEvent) {
		with(event) {
			if (book?.isTownBook() == true) {
				isCancelled = true
			}
		}
	}

	@EventHandler
	fun onBreakLectern(event: BlockBreakEvent) {
		with(event) {
			if (block.type == Material.LECTERN) {
				((block.state as Lectern).inventory as LecternInventory).book?.let {
					if (it.isTownBook()) {
						isCancelled = true
					}
				}
			}
		}
	}

	@EventHandler
	fun onPlayerOpenBook(event: PlayerInteractEvent) {
		with(event) {
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				val isLectern = clickedBlock?.type == Material.LECTERN
				val isItemBook = item?.isTownBook() == true

				val townId: Int? = if (!isLectern && isItemBook) {
					item?.townId
				} else if (isLectern) {
					clickedBlock?.let { block ->
						if ((block.blockData as LecternData).hasBook()) {
							val book = ((block.state as Lectern).inventory as LecternInventory).book
							book?.townId
						} else {
							null
						}
					}
				} else {
					null
				}

				townId?.let { id ->
					TownController[id]?.let {
						player.openTownBook(it)
						isCancelled = true
					}
				}
			}
		}
	}
}