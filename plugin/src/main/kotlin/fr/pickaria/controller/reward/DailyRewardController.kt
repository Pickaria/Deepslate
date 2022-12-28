package fr.pickaria.controller.reward

import fr.pickaria.controller.reward.events.DailyRewardReadyEvent
import fr.pickaria.model.reward.*
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Add daily points to a player and fire an event when the daily amount is reached.
 */
fun Player.addDailyPoint(points: Int) {
	transaction {
		val dailyReward = DailyReward.find {
			DailyRewards.playerUuid eq uniqueId
		}.firstOrNull() ?: DailyReward.new {
			playerUuid = uniqueId
		}

		val today = Clock.System.todayIn(currentSystemDefault())
		val previousCanCollect = dailyReward.canCollect() > 0

		if (dailyReward.lastDay == today) {
			dailyReward.dailyPoints += points
		} else {
			dailyReward.dailyPoints = points
			dailyReward.lastDay = today
		}

		if (!previousCanCollect && dailyReward.canCollect() > 0) {
			DailyRewardReadyEvent(this@addDailyPoint, dailyReward.dailyPoints).callEvent()
		}
	}
}

val Player.dailyReward
	get() = transaction {
		DailyReward.find {
			DailyRewards.playerUuid eq uniqueId
		}.firstOrNull() ?: DailyReward.new {
			playerUuid = uniqueId
		}
	}

/**
 * Checks if the player can collect a reward.
 * @return The amount of rewards remaining to collect today.
 */
fun DailyReward.canCollect(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int {
	val remainingToCollect = remainingToCollect(date)

	return if (lastDay == date && dailyPoints(date) >= rewardConfig.dailyPointsToCollect && remainingToCollect > 0) {
		remainingToCollect
	} else {
		0
	}
}

/**
 * Returns the total amount of rewards remaining to claim.
 * Including rewards that are not yet ready to collect.
 */
fun DailyReward.remainingToCollect(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int =
	if (lastCollectedDate == date) {
		rewardCount(date) - collectedToday
	} else {
		rewardCount(date)
	}

/**
 * Returns the amount of points before the next reward.
 */
fun DailyReward.dailyPoints(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int =
	if (lastCollectedDate == date) {
		dailyPoints - (collectedToday * rewardConfig.dailyPointsToCollect)
	} else if (lastDay == date) {
		dailyPoints
	} else {
		0
	}

/**
 * Returns the amount of points before the next reward.
 */
fun DailyReward.totalDailyPoints(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int =
	if (lastDay == date) {
		dailyPoints
	} else {
		0
	}

/**
 * Returns the amount of collected rewards.
 */
fun DailyReward.collected(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int =
	if (lastCollectedDate == date) {
		collectedToday
	} else {
		0
	}

/**
 * Returns the streak of collected rewards taking date into account.
 */
fun DailyReward.streak(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int =
	when (lastCollectedDate) {
		date.minus(1, DateTimeUnit.DAY) -> {
			streak
		}

		date -> {
			streak - 1
		}

		else -> {
			0
		}
	}

fun DailyReward.collect(): Boolean {
	val today = Clock.System.todayIn(currentSystemDefault())

	return if (canCollect(today) > 0) {
		transaction {
			// Increment streak only if last collected was yesterday
			if (lastCollectedDate == today.minus(1, DateTimeUnit.DAY)) {
				streak = streak() + 1
			}

			collectedToday = if (today == lastCollectedDate) {
				collectedToday + 1
			} else {
				lastCollectedDate = today
				1
			}
		}

		true
	} else {
		false
	}
}

/**
 * Returns the amount of rewards a player can claim a specific day.
 */
fun DailyReward.rewardCount(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): Int {
	val streakNumber = streak(date) / rewardConfig.streakRewardEvery
	var amount = rewardConfig.rewardPerDay

	if (rewardConfig.specialDays.contains(date.toMonthDay())) {
		amount++
	}

	if (streakNumber > 0) {
		amount++
	}

	return amount
}

fun DailyReward.rewards(date: LocalDate = Clock.System.todayIn(currentSystemDefault())): List<Reward> {
	val streakNumber = streak(date) / rewardConfig.streakRewardEvery

	val streakReward = if (streakNumber > 0) {
		rewardConfig.rewards[rewardConfig.streakRewards[streakNumber % rewardConfig.streakRewards.size]]
	} else {
		null
	}

	val specialReward = rewardConfig.rewards[rewardConfig.specialDays[date.toMonthDay()]]
	val defaultReward = rewardConfig.rewards[rewardConfig.defaultReward]

	return (1..rewardConfig.rewardPerDay).mapNotNull {
		defaultReward
	} + listOfNotNull(streakReward, specialReward)
}