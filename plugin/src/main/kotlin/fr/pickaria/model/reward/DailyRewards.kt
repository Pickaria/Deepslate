package fr.pickaria.model.reward

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object DailyRewards : IntIdTable() {
	val playerUuid = uuid("player_uuid").uniqueIndex()
	val dailyPoints = integer("daily_points").default(0)
	val lastDay = date("last_day").default(Clock.System.todayIn(currentSystemDefault()))
	val lastCollectedDate = date("last_collected_date").nullable().default(null)
	val streak = integer("streak").default(0)
	val collectedToday = integer("collected_today").default(0)
}

class DailyReward(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<DailyReward>(DailyRewards)

	var playerUuid by DailyRewards.playerUuid
	var dailyPoints by DailyRewards.dailyPoints
	var lastDay by DailyRewards.lastDay
	var lastCollectedDate by DailyRewards.lastCollectedDate
	var streak by DailyRewards.streak
	var collectedToday by DailyRewards.collectedToday
}
