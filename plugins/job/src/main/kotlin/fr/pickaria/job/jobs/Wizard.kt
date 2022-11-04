package fr.pickaria.job.jobs

import fr.pickaria.job.hasJob
import fr.pickaria.job.jobConfig
import fr.pickaria.job.jobPayPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent

class Wizard: Listener {
	companion object {
		private const val JOB_NAME = "wizard"
		private val config = jobConfig.jobs[JOB_NAME]!!
	}

	private val levels = mapOf(
		1 to 0.15,
		2 to 0.175,
		3 to 0.2,
	)

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEnchantItem(event: EnchantItemEvent) {
		if (event.isCancelled || !(event.enchanter hasJob JOB_NAME)) return

		jobPayPlayer(event.enchanter, levels[event.whichButton()] ?: 0.15, config, 1)
	}
}