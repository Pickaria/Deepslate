package fr.pickaria.model.town

import fr.pickaria.model.now
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

internal object Towns : Table() {
	val id = integer("id").autoIncrement()
	val identifier = varchar("town_id", 32).uniqueIndex()
	val flag = binary("flag", 1024)
	val creationDate = datetime("creation_date").clientDefault { now() }

	override val primaryKey = PrimaryKey(id)
}

class Town(private val row: ResultRow) {
	companion object {
		fun create(townId: String, flag: ItemStack) = transaction {
			Towns.insert {
				it[identifier] = townId
				it[Towns.flag] = flag.serializeAsBytes()
			}.resultedValues?.firstOrNull()
		}?.let { Town(it) }

		fun get(limit: Int, offset: Long = 0) = transaction {
			Towns.selectAll()
				.limit(limit, offset)
				.map { Town(it) }
		}

		fun get(id: Int) = transaction {
			Towns.select {
				Towns.id eq id
			}.firstOrNull()
		}?.let { Town(it) }

		fun get(identifier: String) = transaction {
			Towns.select {
				Towns.identifier eq identifier
			}.firstOrNull()
		}?.let { Town(it) }

		fun count() = transaction {
			Towns.selectAll().count()
		}
	}

	private val whereClause =
		{ Towns.id eq this.id }

	fun delete() = transaction {
		Towns.deleteWhere { whereClause() }
	}

	val id: Int
		get() = row[Towns.id]

	var townId: String
		get() = row[Towns.identifier]
		set(value) = transaction {
			Towns.update({ whereClause() }) {
				it[identifier] = value
			}
		}

	var flag: ItemStack
		get() = ItemStack.deserializeBytes(row[Towns.flag])
		set(value) = transaction {
			Towns.update({ whereClause() }) {
				it[Towns.flag] = value.serializeAsBytes()
			}
		}
}