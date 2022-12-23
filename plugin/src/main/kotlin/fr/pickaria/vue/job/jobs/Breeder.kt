package fr.pickaria.vue.job.jobs

import fr.pickaria.controller.job.hasJob
import fr.pickaria.controller.job.jobPayPlayer
import fr.pickaria.model.job.JobType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityBreedEvent
import org.bukkit.event.entity.EntityTameEvent

class Breeder : Listener {
	companion object {
		private val config = JobType.BREEDER.toJob()
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEntityBreed(event: EntityBreedEvent) {
		if (event.breeder is Player) {
			val player = event.breeder as Player
			if (event.isCancelled || !(player hasJob JobType.BREEDER)) return

			jobPayPlayer(player, 0.2, config, 1)
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEntityTame(event: EntityTameEvent) {
		val player = event.owner as Player
		if (event.isCancelled || !(player hasJob JobType.BREEDER)) return

		jobPayPlayer(player, 0.3, config, 1)
	}
}