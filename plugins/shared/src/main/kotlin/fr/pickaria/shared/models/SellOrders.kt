package fr.pickaria.shared.models

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

internal object SellOrders : Table() {
	val id = integer("id").autoIncrement()
	val seller = uuid("seller")
	val item = binary("item", 4096)
	val price = double("price").default(0.0)
	val amount = integer("amount").default(1)

	override val primaryKey = PrimaryKey(id)
}

class SellOrder private constructor(private val row: ResultRow) {
	companion object {
		fun create(seller: OfflinePlayer, item: ItemStack, price: Double = 0.0) = transaction {
			SellOrders.insert {
				it[this.seller] = seller.uniqueId
				it[this.item] = item.asOne().serializeAsBytes()
				it[this.price] = price
				it[this.amount] = item.amount
			}.resultedValues?.firstOrNull()
		}?.let {
			SellOrder(it)
		}

		/**
		 * Get one sell order by id.
		 */
		fun get(id: Int) = SellOrder { SellOrders.id eq id }

		/**
		 * Get all specific item's sell orders placed by a player.
		 */
		fun get(seller: OfflinePlayer, item: ItemStack): List<SellOrder> {
			val bytes = item.serializeAsBytes()

			return SellOrders.select {
				(SellOrders.seller eq seller.uniqueId) and (SellOrders.item eq bytes)
			}.map {
				SellOrder(it)
			}
		}

		/**
		 * Get all specific item's sell orders.
		 */
		fun get(item: ItemStack): List<SellOrder> {
			val bytes = item.serializeAsBytes()

			return SellOrders.select {
				SellOrders.item eq bytes
			}.map {
				SellOrder(it)
			}
		}

		/**
		 * Get all specific material's sell orders.
		 */
		fun get(material: Material): List<SellOrder> {
			val item = ItemStack(material).serializeAsBytes()

			return SellOrders.select {
				SellOrders.item eq item
			}.map {
				SellOrder(it)
			}
		}

		/**
		 * Get all sell orders of a player.
		 */
		fun get(seller: OfflinePlayer) =
			SellOrders.select {
				SellOrders.seller eq seller.uniqueId
			}.map {
				SellOrder(it)
			}
	}

	private constructor(whereClause: SqlExpressionBuilder.() -> Op<Boolean>) : this(
		transaction {
			SellOrders.select {
				whereClause()
			}.first()
		}
	)

	private val whereClause = { SellOrders.id eq this.id }

	val id: Int
		get() = row[SellOrders.id]

	val seller: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(row[SellOrders.seller])

	val item: ItemStack
		get() = ItemStack.deserializeBytes(row[SellOrders.item])

	val price: Double
		get() = row[SellOrders.price]

	val amount: Int
		get() = row[SellOrders.amount]

	val material: Material
		get() = item.type
}