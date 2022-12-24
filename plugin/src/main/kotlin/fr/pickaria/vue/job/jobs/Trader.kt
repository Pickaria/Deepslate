package fr.pickaria.vue.job.jobs

import fr.pickaria.controller.job.hasJob
import fr.pickaria.controller.job.jobPayPlayer
import fr.pickaria.model.job.JobType
import io.papermc.paper.event.player.PlayerTradeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class Trader : Listener {
	companion object {
		private val config = JobType.TRADER.toJob()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onInventoryClick(event: PlayerTradeEvent) {
		if (!event.isCancelled && event.player hasJob JobType.TRADER) {
			jobPayPlayer(event.player, 0.2, config, 1)
		}
	}
}