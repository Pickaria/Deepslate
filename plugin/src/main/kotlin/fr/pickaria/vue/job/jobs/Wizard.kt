package fr.pickaria.vue.job.jobs

import fr.pickaria.controller.job.hasJob
import fr.pickaria.controller.job.jobPayPlayer
import fr.pickaria.model.job.JobType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent

class Wizard : Listener {
	companion object {
		private val config = JobType.WIZARD.toJob()
	}

	private val levels = mapOf(
		1 to 0.15,
		2 to 0.175,
		3 to 0.2,
	)

	@EventHandler(priority = EventPriority.MONITOR)
	fun onEnchantItem(event: EnchantItemEvent) {
		if (event.isCancelled || !(event.enchanter hasJob JobType.WIZARD)) return

		jobPayPlayer(event.enchanter, levels[event.whichButton()] ?: 0.15, config, 1)
	}
}