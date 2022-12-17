package fr.pickaria.database.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.*

internal object PlayerRewards : Table() {
	val playerUuid = uuid("player_uuid")
	val rewardStreak = integer("reward_streak").default(0)
	val lastReward = date("last_reward").nullable()

	override val primaryKey = PrimaryKey(playerUuid)
}

class PlayerReward(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, streak: Int = 0, last: LocalDate? = null) = transaction {
			PlayerRewards.insert {
				it[playerUuid] = playerId
				it[rewardStreak] = streak
				it[lastReward] = last
			}.resultedValues?.firstOrNull()
		}?.let { PlayerReward(it) }

		fun get(playerId: UUID) = transaction {
			PlayerRewards.select {
				PlayerRewards.playerUuid eq playerId
			}.firstOrNull()
		}?.let { PlayerReward(it) }

		// Case-specific functions
	}

	private val whereClause = { PlayerRewards.playerUuid eq this.playerUuid }

	val playerUuid: UUID
		get() = row[PlayerRewards.playerUuid]

	var rewardStreak: Int
		get() = row[PlayerRewards.rewardStreak]
		set(value) = transaction {
			PlayerRewards.update({ whereClause() }) {
				it[PlayerRewards.rewardStreak] = value
			}
		}

	var lastReward: LocalDate?
		get() = row[PlayerRewards.lastReward]
		set(value) = transaction {
			PlayerRewards.update({ whereClause() }) {
				it[PlayerRewards.lastReward] = value
			}
		}
}