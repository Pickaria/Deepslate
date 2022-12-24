package fr.pickaria.model.job

import fr.pickaria.model.now
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal object Jobs : Table() {
	val playerUuid = uuid("player_uuid")
	val job = enumerationByName<JobType>("job", 9)
	val experience = double("experience").default(0.0)
	val lastUsed = datetime("last_used").default(now())
	val active = bool("active").default(false)
	val ascentPoints = integer("ascent_points").default(0)

	override val primaryKey = PrimaryKey(playerUuid, job)
}

class JobModel(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, job: JobType, active: Boolean = false) = transaction {
			Jobs.insert {
				it[playerUuid] = playerId
				it[Jobs.job] = job
				it[Jobs.active] = active
			}.resultedValues?.firstOrNull()
		}?.let { JobModel(it) }

		fun get(playerId: UUID, job: JobType) = transaction {
			Jobs.select {
				(Jobs.playerUuid eq playerId) and (Jobs.job eq job)
			}.firstOrNull()
		}?.let { JobModel(it) }

		fun get(playerId: UUID) = transaction {
			Jobs.select {
				Jobs.playerUuid eq playerId
			}.map { JobModel(it) }
		}
	}

	private val whereClause =
		{ (Jobs.playerUuid eq this@JobModel.playerUuid) and (Jobs.job eq this@JobModel.job) }

	val playerUuid: UUID
		get() = row[Jobs.playerUuid]

	val job: JobType
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