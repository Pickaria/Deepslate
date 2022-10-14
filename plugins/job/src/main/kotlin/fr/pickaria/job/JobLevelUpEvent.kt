package fr.pickaria.job

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class JobLevelUpEvent internal constructor(
	val player: Player,
	val type: LevelUpType,
	val job: JobConfig.Configuration,
	val level: Int
) :
	Event(), Cancellable {
	private var isCancelled = false

	override fun getHandlers(): HandlerList {
		return HANDLERS
	}

	override fun isCancelled(): Boolean {
		return isCancelled
	}

	override fun setCancelled(isCancelled: Boolean) {
		this.isCancelled = isCancelled
	}

	companion object {
		val HANDLERS = HandlerList()

		@JvmStatic
		fun getHandlerList(): HandlerList {
			return HANDLERS
		}
	}
}