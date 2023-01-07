package fr.pickaria.controller.reward

import fr.pickaria.model.reward.DailyReward
import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toMonthDay
import kotlinx.datetime.*

data class DailyRewardInfo(
	// Amount of collected rewards for this day
	val collected: Int,
	// Date of the daily reward
	val date: LocalDate,
	// Points collected for this day
	val totalPoints: Int,
	// Total amount of rewards to collect
	val rewardCount: Int,
	// List of all rewards to be collected
	val rewards: List<Reward>,
	// This is a special day
	val isSpecialDay: Boolean,
	// Current streak
	val streak: Int,
	// This day has a streak reward
	val hasStreakReward: Boolean,
)

fun DailyRewardInfo.isCollectionDay(): Boolean {
	val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
	return date == today
}

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
		hasStreakReward = hasStreakReward,
		isSpecialDay = isSpecialDay,
		streak = actualStreak,
		rewards = rewards,
		rewardCount = rewards.size,
	)
}