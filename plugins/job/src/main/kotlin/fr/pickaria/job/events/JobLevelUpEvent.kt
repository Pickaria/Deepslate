package fr.pickaria.job.events

import fr.pickaria.job.JobConfig
import fr.pickaria.job.LevelUpType
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class JobLevelUpEvent internal constructor(
	val player: Player,
	val type: LevelUpType,
	val job: JobConfig.Configuration,
	val level: Int
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