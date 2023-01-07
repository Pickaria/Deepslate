package fr.pickaria.controller.reward

import fr.pickaria.model.reward.DailyReward
import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toMonthDay
import kotlinx.datetime.*
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.min

data class DailyRewardInfo(
	/**
	 * Amount of collected rewards for this day.
	 */
	val collected: Int,
	/**
	 * Date of the daily reward.
	 */
	val date: LocalDate,
	/**
	 * Points collected for this day.
	 */
	val totalPoints: Int,
	/**
	 * Total amount of rewards to collect.
	 */
	val rewardCount: Int,
	/**
	 * List of all rewards to be collected.
	 */
	val rewards: List<Reward>,
	/**
	 * This is a special day.
	 */
	val isSpecialDay: Boolean,
	/**
	 * Current streak.
	 */
	val streak: Int,
	/**
	 * This day has a streak reward.
	 */
	val hasStreakReward: Boolean,
	/**
	 * Weather the streak is validated for this day.
	 */
	val streakValidated: Boolean,

	private val dailyReward: DailyReward,
) {
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
fun DailyReward.toInfo(date: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())): DailyRewardInfo {
	val isSpecialDay = rewardConfig.specialDays.contains(date.toMonthDay())
	val actualStreak = when (lastCollectedDate) {
		date.minus(1, DateTimeUnit.DAY) -> streak
		date -> streak - 1
		else -> 0
	}
	val hasStreakReward =
		actualStreak >= rewardConfig.streakRewardEvery && actualStreak % rewardConfig.streakRewardEvery == 0
	val collected = if (lastCollectedDate == date) collectedToday else 0
	val totalPoints = if (lastDay == date) dailyPoints else 0
	val streakValidated = lastCollectedDate == date

	// Get all rewards
	val streakReward = if (hasStreakReward) {
		val streakNumber = actualStreak / rewardConfig.streakRewardEvery - 1
		val streakReward = rewardConfig.streakRewards.getOrNull(streakNumber % rewardConfig.streakRewards.size)
		rewardConfig.rewards[streakReward]
	} else {
		null
	}

	val specialReward = rewardConfig.rewards[rewardConfig.specialDays[date.toMonthDay()]]
	val defaultReward = rewardConfig.rewards[rewardConfig.defaultReward]

	val rewards = (1..rewardConfig.rewardPerDay).mapNotNull {
		defaultReward
	} + listOfNotNull(streakReward, specialReward)


	return DailyRewardInfo(
		collected = collected,
		date = date,
		totalPoints = totalPoints,
		rewardCount = rewards.size,
		rewards = rewards,
		isSpecialDay = isSpecialDay,
		streak = actualStreak,
		hasStreakReward = hasStreakReward,
		streakValidated = streakValidated,
		dailyReward = this,
	)
}
