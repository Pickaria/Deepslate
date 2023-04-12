package fr.pickaria.controller.reward

import fr.pickaria.controller.reward.events.DailyRewardReadyEvent
import fr.pickaria.model.reward.DailyReward
import fr.pickaria.model.reward.DailyRewards
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.todayIn
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Add daily points to a player and fire an event when the daily amount is reached.
 * @param points Amount of points to add to the player's daily rewards.
 * @see DailyReward
 * @see DailyRewardInfo
 */
fun OfflinePlayer.addDailyPoint(points: Int) {
	transaction {
		val dailyReward = DailyReward.find {
			DailyRewards.playerUuid eq uniqueId
		}.firstOrNull() ?: DailyReward.new {
			playerUuid = uniqueId
		}

		val info = dailyReward.toInfo()
		val today = Clock.System.todayIn(currentSystemDefault())
		val previousCanCollect = info.canCollectRewards > 0

		if (info.canCollectForDate) {
			dailyReward.dailyPoints += points
		} else {
			dailyReward.dailyPoints = points
			dailyReward.lastDay = today
		}

		if (!previousCanCollect && dailyReward.toInfo().canCollectRewards > 0) {
			DailyRewardReadyEvent(this@addDailyPoint, dailyReward.dailyPoints).callEvent()
		}
	}
}

/**
 * Gets the daily reward of the current OfflinePlayer.
 * @see DailyReward
 * @see DailyRewardInfo
 */
val OfflinePlayer.dailyReward
	get() = transaction {
		DailyReward.find {
			DailyRewards.playerUuid eq uniqueId
		}.firstOrNull() ?: DailyReward.new {
			playerUuid = uniqueId
		}
	}
