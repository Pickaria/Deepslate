package fr.pickaria.controller.reward.events

import org.bukkit.OfflinePlayer
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class DailyRewardReadyEvent internal constructor(
	val offlinePlayer: OfflinePlayer,
	val points: Int,
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