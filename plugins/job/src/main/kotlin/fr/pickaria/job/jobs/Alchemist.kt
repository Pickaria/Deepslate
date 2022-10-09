package fr.pickaria.job.jobs

import fr.pickaria.job.jobConfig
import fr.pickaria.job.jobController
import fr.pickaria.job.jobPayPlayer
import org.bukkit.Bukkit.getServer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import java.util.*

class Alchemist: Listener {
	companion object {
		private const val JOB_NAME = "alchemist"
		private val config = jobConfig.jobs[JOB_NAME]!!
	}

	private fun isPotion(itemStack: ItemStack): Boolean {
		return if (itemStack.type == Material.POTION ||
			itemStack.type == Material.SPLASH_POTION ||
			itemStack.type == Material.LINGERING_POTION) {
			(itemStack.itemMeta as PotionMeta).basePotionData.type.effectType != null
		} else {
			false
		}
	}

	// Store ownership oof brewing stands
	private val brewingStands: MutableMap<Location, UUID> = mutableMapOf()

	@EventHandler
	fun onPlayerInteract(event: PlayerInteractEvent) {
		if (event.clickedBlock == null) return

		val uniqueId = event.player.uniqueId
		if (!jobController.hasJob(uniqueId, JOB_NAME)) return

		brewingStands.putIfAbsent(event.clickedBlock!!.location, uniqueId)
	}

	@EventHandler
	fun onPlayerInteract(event: BlockBreakEvent) {
		brewingStands.remove(event.block.location)
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onBrew(event: BrewEvent) {
		val location = event.block.location
		val uniqueId = brewingStands[location]
		if (event.isCancelled || uniqueId == null || !jobController.hasJob(uniqueId, JOB_NAME)) return

		event.results.forEach {
			if (isPotion(it)) {
				getServer().getPlayer(uniqueId)?.let { player ->
					jobPayPlayer(player, 0.15, config, 1)
				}
			}
		}
	}
}