package fr.pickaria.controller.job.events

import fr.pickaria.model.job.Job
import fr.pickaria.model.job.JobRank
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class JobAscentEvent(
	val player: Player,
	val job: Job,
	/**
	 * Ascent points obtained from the ascent.
	 */
	val ascentPointsObtained: Int,
	/**
	 * Total amount of ascent points.
	 */
	val ascentPoints: Int,
	/**
	 * New rank of the player.
	 */
	val rank: JobRank?,
) : Event(), Cancellable {
	private var isCancelled = false

	override fun getHandlers() = HANDLERS

	override fun isCancelled() = isCancelled

	override fun setCancelled(isCancelled: Boolean) {
		this.isCancelled = isCancelled
	}

	companion object {
		private val HANDLERS = HandlerList()

		@JvmStatic
		fun getHandlerList() = HANDLERS
	}
}