package fr.pickaria.shared.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction

internal object ShopItems : Table() {
	val serializedItemStack = binary("serialized_item_stack", 4096)
	val amount = integer("amount")

	override val primaryKey = PrimaryKey(serializedItemStack)
}

class ShopItem(private val row: ResultRow) {
	companion object {
		fun create(serializedItemStack: ByteArray, amount: Int = 1) = transaction {
			ShopItems.insert {
				it[ShopItems.serializedItemStack] = serializedItemStack
				it[ShopItems.amount] = amount
			}.resultedValues?.firstOrNull()
		}?.let { ShopItem(it) }

		fun get(serializedItemStack: ByteArray) = transaction {
			ShopItems.select {
				ShopItems.serializedItemStack eq serializedItemStack
			}.firstOrNull()
		}?.let { ShopItem(it) }
	}

	private val whereClause = { ShopItems.serializedItemStack eq this@ShopItem.serializedItemStack }

	val serializedItemStack: ByteArray
		get() = row[ShopItems.serializedItemStack]

	var amount: Int
		get() = row[ShopItems.amount]
		set(value) = transaction {
			ShopItems.update({ whereClause() }) {
				it[ShopItems.amount] = value
			}
		}
}