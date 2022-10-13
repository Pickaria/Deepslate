package fr.pickaria.job.jobs

import fr.pickaria.job.jobConfig
import fr.pickaria.job.jobController
import fr.pickaria.job.jobPayPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType

class Trader: Listener {
	companion object {
		private const val JOB_NAME = "trader"
		private val config = jobConfig.jobs[JOB_NAME]!!
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onInventoryClick(event: InventoryClickEvent) {
		val player = event.whoClicked as Player

		if (!event.isCancelled &&
			jobController.hasJob(player.uniqueId, JOB_NAME) &&
			event.inventory.type == InventoryType.MERCHANT &&
			event.slotType == InventoryType.SlotType.RESULT) {

			jobPayPlayer(player, 0.2, config, 1)
		}
	}
}