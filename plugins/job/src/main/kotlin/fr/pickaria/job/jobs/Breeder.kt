package fr.pickaria.job.jobs

import fr.pickaria.job.jobConfig
import fr.pickaria.job.jobController
import fr.pickaria.job.jobPayPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityTameEvent

class Breeder: Listener {
	companion object {
		private const val JOB_NAME = "breeder"
		private val config = jobConfig.jobs[JOB_NAME]!!
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEntityBreed(event: EntityBreedEvent) {
		if (event.breeder is Player) {
			val player = event.breeder as Player
			if (event.isCancelled || !jobController.hasJob(player.uniqueId, JOB_NAME)) return

			jobPayPlayer(player, 0.2, config, 1)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEntityTame(event: EntityTameEvent) {
		val player = event.owner as Player
		if (event.isCancelled || !jobController.hasJob(player.uniqueId, JOB_NAME)) return

		jobPayPlayer(player, 0.3, config, 1)
	}
}