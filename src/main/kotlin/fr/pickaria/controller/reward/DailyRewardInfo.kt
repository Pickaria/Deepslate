package fr.pickaria.controller.reward

import fr.pickaria.model.reward.DailyReward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toMonthDay
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.min

data class DailyRewardInfo(
	/**
	 * Date of the daily reward.
	 */
	val date: LocalDate,

	private val dailyReward: DailyReward,
) {
	/**
	 * This is a special day.
	 */
	val isSpecialDay by lazy {
		rewardConfig.specialDays.contains(date.toMonthDay())
	}

	/**
	 * Current streak.
	 */
	val streak by lazy {
		when (dailyReward.lastCollectedDate) {
			date.minus(1, DateTimeUnit.DAY) -> dailyReward.streak
			date -> dailyReward.streak - 1
			else -> 0
		}
	}

	/**
	 * This day has a streak reward.
	 */
	val hasStreakReward by lazy {
		streak >= rewardConfig.streakRewardEvery && streak % rewardConfig.streakRewardEvery == 0
	}

	/**
	 * Amount of collected rewards for this day.
	 */
	val collected by lazy {
		if (dailyReward.lastCollectedDate == date) dailyReward.collectedToday else 0
	}

	/**
	 * Points collected for this day.
	 */
	val totalPoints by lazy {
		if (dailyReward.lastDay == date) dailyReward.dailyPoints else 0
	}

	/**
	 * Weather the streak is validated for this day.
	 */
	val streakValidated by lazy {
		dailyReward.lastCollectedDate == date
	}

	/**
	 * List of all rewards to be collected.
	 */
	val rewards by lazy {
		val streakReward = if (hasStreakReward) {
			val streakNumber = streak / rewardConfig.streakRewardEvery - 1
			val streakReward = rewardConfig.streakRewards.getOrNull(streakNumber % rewardConfig.streakRewards.size)
			rewardConfig.rewards[streakReward]
		} else {
			null
		}

		val specialReward = rewardConfig.rewards[rewardConfig.specialDays[date.toMonthDay()]]
		val defaultReward = rewardConfig.rewards[rewardConfig.defaultReward]

		(1..rewardConfig.rewardPerDay).mapNotNull {
			defaultReward
		} + listOfNotNull(streakReward, specialReward)
	}

	/**
	 * Total amount of rewards to collect.
	 */
	val rewardCount by lazy {
		rewards.size
	}

	/**
	 * If the points can be collected for the last day or it must be collected for a new day.
	 */
	val canCollectForDate by lazy {
		dailyReward.lastDay == date
	}

	/**
	 * Remaining rewards to be collected.
	 */
	val remainingRewards by lazy {
		rewardCount - collected
	}

	/**
	 * Total amount of points required to collect the next reward.
	 */
	val pointsForNextReward by lazy {
		min((collected + 1) * rewardConfig.dailyPointsToCollect, rewardCount * rewardConfig.dailyPointsToCollect)
	}

	/**
	 * Amounts of points remaining to get in order to get the next reward.
	 */
	val remainingPointsForNextReward by lazy {
		pointsForNextReward - totalPoints
	}

	/**
	 * If today is the same day as the reward's day.
	 */
	val isCollectionDay by lazy {
		val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
		date == today
	}

	/**
	 * Amount of rewards that can be collected with the current amount of points.
	 *
	 * Does not take into account the current day so remember to check use `isCollectionDay` as well.
	 * @see isCollectionDay
	 */
	val canCollectRewards by lazy {
		totalPoints / pointsForNextReward
	}

	/**
	 * Gets the next reward to be collected.
	 */
	val nextReward by lazy {
		rewards.getOrNull(collected)
	}

	fun collect(): Boolean {
		val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

		if (canCollectRewards > 0) {
			transaction {
				dailyReward.streak = streak + 1
				dailyReward.collectedToday = collected + 1
				dailyReward.lastCollectedDate = today
			}

			return true
		}

		return false
	}
}

/**
 * Converts a DailyReward to a DailyRewardInfo.
 *
 * The informations are calculated based on the given date.
 * @param date The date of the daily rewards' information. Defaults to today.
 * @see DailyReward
 * @see DailyRewardInfo
 */
fun DailyReward.toInfo(date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())): DailyRewardInfo =
	DailyRewardInfo(
		date = date,
		dailyReward = this,
	)
