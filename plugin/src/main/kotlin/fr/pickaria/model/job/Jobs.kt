package fr.pickaria.model.job

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal object Jobs : Table() {
	val playerUuid = uuid("player_uuid")
	val job = varchar("job", 16)
	val experience = double("experience").default(0.0)
	val lastUsed = datetime("last_used").default(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()))
	val active = bool("active").default(false)
	val ascentPoints = integer("ascent_points").default(0)

	override val primaryKey = PrimaryKey(playerUuid, job)
}

class Job(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, job: String, active: Boolean = false) = transaction {
			Jobs.insert {
				it[playerUuid] = playerId
				it[Jobs.job] = job
				it[Jobs.active] = active
			}.resultedValues?.firstOrNull()
		}?.let { Job(it) }

		fun get(playerId: UUID, job: String) = transaction {
			Jobs.select {
				(Jobs.playerUuid eq playerId) and (Jobs.job eq job)
			}.firstOrNull()
		}?.let { Job(it) }

		fun get(playerId: UUID) = transaction {
			Jobs.select {
				Jobs.playerUuid eq playerId
			}.map { Job(it) }
		}
	}

	private val whereClause =
		{ (Jobs.playerUuid eq this@Job.playerUuid) and (Jobs.job eq this@Job.job) }

	val playerUuid: UUID
		get() = row[Jobs.playerUuid]

	val job: String
		get() = row[Jobs.job]

	var experience: Double
		get() = row[Jobs.experience]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[experience] = value
			}
		}

	var active: Boolean
		get() = row[Jobs.active]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[active] = value
			}
		}

	var lastUsed: LocalDateTime
		get() = row[Jobs.lastUsed]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[lastUsed] = value
			}
		}

	var ascentPoints: Int
		get() = row[Jobs.ascentPoints]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[ascentPoints] = value
			}
		}
}