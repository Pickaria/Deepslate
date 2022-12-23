package fr.pickaria.job.jobs

import fr.pickaria.job.Config
import fr.pickaria.job.hasJob
import fr.pickaria.job.jobPayPlayer
import io.papermc.paper.event.player.PlayerTradeEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

class Trader : Listener {
	companion object {
		private const val JOB_NAME = "trader"
		private val config = Config.jobs[JOB_NAME]!!
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onInventoryClick(event: PlayerTradeEvent) {
		if (!event.isCancelled && event.player hasJob JOB_NAME) {
			jobPayPlayer(event.player, 0.2, config, 1)
		}
	}
}