package fr.pickaria.job.jobs

import fr.pickaria.job.Config
import fr.pickaria.job.hasJob
import fr.pickaria.job.jobPayPlayer
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.world.StructureGrowEvent

class Farmer : Listener {
	companion object {
		private const val JOB_NAME = "farmer"
		private val config = Config.jobs[JOB_NAME]!!
	}

	private val crops = mapOf(
		// Ageable
		Material.WHEAT to 0.2,
		Material.CARROTS to 0.2,
		Material.POTATOES to 0.2,
		Material.BEETROOTS to 0.2,
		Material.COCOA_BEANS to 0.2,

		Material.SUGAR_CANE to 0.2,
		Material.BAMBOO to 0.2,

		/*Material.PUMPKIN to 0.1,
		Material.MELON to 0.1,

		Material.MUSHROOM_STEM to 0.1,
		Material.RED_MUSHROOM_BLOCK to 0.1,
		Material.BROWN_MUSHROOM_BLOCK to 0.1,*/
	)

	@EventHandler(priority = EventPriority.MONITOR)
	fun onBlockBreak(event: BlockBreakEvent) {
		if (event.isCancelled) return
		if (!(event.player hasJob JOB_NAME)) return

		crops[event.block.type]?.let {
			(event.block.blockData as? Ageable)?.let { blockData ->
				if (blockData.age == blockData.maximumAge) {
					jobPayPlayer(event.player, it, config, 1)
				}
			} ?: run {
				jobPayPlayer(event.player, it, config, 1)
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	fun onStructureGrow(event: StructureGrowEvent) {
		if (event.isCancelled) return

		event.player?.let {
			if (!(it hasJob JOB_NAME)) return
			if (event.isFromBonemeal) {
				jobPayPlayer(it, 0.05, config)
			}
		}
	}

	/*@EventHandler(priority = EventPriority.MONITOR)
	fun onPlayerInteract(event: PlayerInteractEvent) {
		if (!jobController.hasJob(event.player.uniqueId, JobEnum.FARMER)) return
		val block = event.clickedBlock
		if (block != null) {
			if (block.type == Material.SWEET_BERRY_BUSH) {
				if (block.blockData is Ageable) {
					val blockData = block.blockData as Ageable
					if (blockData.age == blockData.maximumAge) {
						Coin.dropCoin(block.location, 1.0)
					}
				}
			}
		}
	}*/
}