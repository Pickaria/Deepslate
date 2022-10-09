package fr.pickaria.shared.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

internal object Jobs : Table() {
	val playerUuid = uuid("player_uuid")
	val job = varchar("job", 16)
	val experience = integer("experience").default(0)
	val lastUsed = datetime("last_used").default(LocalDateTime.now())
	val active = bool("active").default(false)

	override val primaryKey = PrimaryKey(playerUuid, job)
}

class Job(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, job: String, active: Boolean = false) = transaction {
			Jobs.insert {
				it[playerUuid] = playerId
				it[this.job] = job
				it[this.active] = active
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

	var experience: Int
		get() = row[Jobs.experience]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[Jobs.experience] = value
			}
		}

	var active: Boolean
		get() = row[Jobs.active]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[Jobs.active] = value
			}
		}

	var lastUsed: LocalDateTime
		get() = row[Jobs.lastUsed]
		set(value) = transaction {
			Jobs.update({ whereClause() }) {
				it[Jobs.lastUsed] = value
			}
		}
}