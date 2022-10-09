package fr.pickaria.job.jobs

import fr.pickaria.job.jobConfig
import fr.pickaria.job.jobController
import fr.pickaria.job.jobPayPlayer
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent


class Hunter : Listener {
	companion object {
		private const val JOB_NAME = "hunter"
		private val config = jobConfig.jobs[JOB_NAME]!!
	}

	private val monsters = mapOf(
		// Level 1 mobs
		EntityType.ZOMBIE to Pair(0.2, 1),
		EntityType.SKELETON to Pair(0.2, 1),
		EntityType.SPIDER to Pair(0.2, 1),
		EntityType.SLIME to Pair(0.2, 1),
		EntityType.SILVERFISH to Pair(0.2, 1),
		EntityType.MAGMA_CUBE to Pair(0.2, 1),
		EntityType.ENDERMAN to Pair(0.2, 1),
		EntityType.STRAY to Pair(0.2, 1),
		EntityType.HUSK to Pair(0.2, 1),
		EntityType.ZOMBIE_VILLAGER to Pair(0.2, 1),
		EntityType.DROWNED to Pair(0.2, 1),
		EntityType.PIGLIN to Pair(0.2, 1),
		EntityType.ZOMBIFIED_PIGLIN to Pair(0.2, 1),
		EntityType.CAVE_SPIDER to Pair(0.2, 1),
		EntityType.BLAZE to Pair(0.2, 1),

		// Level 2 mobs
		EntityType.CREEPER to Pair(0.25, 2),
		EntityType.WITCH to Pair(0.25, 2),
		EntityType.ENDERMITE to Pair(0.25, 2),
		EntityType.SHULKER to Pair(0.25, 2),
		EntityType.WITHER_SKELETON to Pair(0.25, 2),
		EntityType.HOGLIN to Pair(0.25, 2),
		EntityType.ZOGLIN to Pair(0.25, 2),
		EntityType.GUARDIAN to Pair(0.25, 2),
		EntityType.PILLAGER to Pair(0.25, 2),
		EntityType.VINDICATOR to Pair(0.25, 2),
		EntityType.EVOKER to Pair(0.25, 2),

		// Level 3 mobs
		EntityType.PIGLIN_BRUTE to Pair(0.3, 5),
		EntityType.PHANTOM to Pair(0.3, 5),
		EntityType.GHAST to Pair(0.3, 5),
		EntityType.VEX to Pair(0.3, 5),
		EntityType.RAVAGER to Pair(0.3, 5),

		// Level 4 mobs
		EntityType.WITHER to Pair(3.0, 50),
		EntityType.ELDER_GUARDIAN to Pair(3.0, 50),
		EntityType.ENDER_DRAGON to Pair(3.0, 50),
	)

	@EventHandler(priority = EventPriority.LOW)
	fun onEntityDeath(event: EntityDeathEvent) {
		event.entity.killer?.let { player ->
			if (jobController.hasJob(player.uniqueId, JOB_NAME)) {
				monsters[event.entityType]?.let {
					jobPayPlayer(player, it.first, config, it.second)
				}
			}
		}
	}
}