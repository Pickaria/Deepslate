package fr.pickaria.controller.job.events

import fr.pickaria.model.job.Job
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class JobJoinedEvent internal constructor(
	val player: Player,
	val job: Job,
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