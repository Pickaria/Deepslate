package fr.pickaria.shared.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

internal object Homes : Table() {
	val playerUuid = uuid("player_uuid")
	val name = varchar("job", 16)
	val world = uuid("world")
	val x = integer("x")
	val y = integer("y")
	val z = integer("z")

	override val primaryKey = PrimaryKey(playerUuid, name)
}

class Home(private val row: ResultRow) {
	companion object {
		fun create(playerId: UUID, name: String) = transaction {
			Homes.insert {
				it[playerUuid] = playerId
				it[this.name] = name
			}.resultedValues?.firstOrNull()
		}?.let { Home(it) }

		fun create(playerId: UUID, name: String, world: UUID, x: Int, y: Int, z: Int) = transaction {
			Homes.insert {
				it[playerUuid] = playerId
				it[this.name] = name
				it[this.world] = world
				it[this.x] = x
				it[this.y] = y
				it[this.z] = z
			}.resultedValues?.firstOrNull()
		}?.let { Home(it) }

		fun get(playerId: UUID, name: String) = transaction {
			Homes.select {
				(Homes.playerUuid eq playerId) and (Homes.name eq name)
			}.firstOrNull()
		}?.let { Home(it) }

		fun get(playerId: UUID) = transaction {
			Homes.select {
				Homes.playerUuid eq playerId
			}.map { Home(it) }
		}
	}

	private val whereClause = { (Homes.playerUuid eq this@Home.playerUuid) and (Homes.name eq this@Home.name) }

	fun remove() {
		Homes.deleteWhere { whereClause() }
	}

	val playerUuid: UUID
		get() = row[Homes.playerUuid]

	val name: String
		get() = row[Homes.name]

	var world: UUID
		get() = row[Homes.world]
		set(value) = transaction {
			Homes.update({ whereClause() }) {
				it[Homes.world] = value
			}
		}

	var x: Int
		get() = row[Homes.x]
		set(value) = transaction {
			Homes.update({ whereClause() }) {
				it[Homes.x] = value
			}
		}

	var y: Int
		get() = row[Homes.y]
		set(value) = transaction {
			Homes.update({ whereClause() }) {
				it[Homes.y] = value
			}
		}

	var z: Int
		get() = row[Homes.z]
		set(value) = transaction {
			Homes.update({ whereClause() }) {
				it[Homes.z] = value
			}
		}
}