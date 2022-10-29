package fr.pickaria.shared.models

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

internal object SellOrders : Table() {
	val id = integer("id").autoIncrement()
	val seller = uuid("seller")
	val material = varchar("material", 256)
	val price = double("price").default(0.0)
	val amount = integer("amount").default(1)

	override val primaryKey = PrimaryKey(id)
}

internal val DEFAULT_MATERIAL = Material.BARRIER

class SellOrder private constructor(private val row: ResultRow) {
	companion object {
		fun create(seller: OfflinePlayer, material: Material, amount: Int = 1, price: Double = 1.0) = transaction {
			SellOrders.insert {
				it[this.seller] = seller.uniqueId
				it[this.material] = material.name
				it[this.price] = price
				it[this.amount] = amount
			}.resultedValues?.firstOrNull()
		}?.let {
			SellOrder(it)
		}

		/**
		 * Get one sell order by id.
		 */
		fun get(id: Int) = transaction {
			SellOrder { SellOrders.id eq id }
		}

		/**
		 * Get all specific item's sell orders placed by a player.
		 */
		fun get(seller: OfflinePlayer, material: Material): List<SellOrder> = transaction {
			SellOrders.select {
				(SellOrders.seller eq seller.uniqueId) and (SellOrders.material eq material.name)
			}.map {
				SellOrder(it)
			}
		}

		/**
		 * Get all specific material's sell orders.
		 */
		fun get(material: Material): List<SellOrder> = transaction {
			SellOrders.select {
				SellOrders.material eq material.name
			}.map {
				SellOrder(it)
			}
		}

		fun get(): List<Order> = transaction {
			val maxPrice = SellOrders.price.max()
			val minPrice = SellOrders.price.min()
			val avgPrice = SellOrders.price.avg()
			val sumAmount = SellOrders.amount.sum()

			SellOrders
				.slice(SellOrders.material, sumAmount, maxPrice, minPrice, avgPrice)
				.selectAll()
				.groupBy(SellOrders.material)
				.map {
					Order(
						Material.getMaterial(it[SellOrders.material]) ?: DEFAULT_MATERIAL,
						it[sumAmount] ?: 0,
						it[maxPrice] ?: 0.0,
						it[minPrice] ?: 0.0,
						it[avgPrice]?.toDouble() ?: 0.0,
					)
				}
		}

		/**
		 * Get all sell orders of a player.
		 */
		fun get(seller: OfflinePlayer) = transaction {
			SellOrders.select {
				SellOrders.seller eq seller.uniqueId
			}.map {
				SellOrder(it)
			}
		}

		fun getPrices(material: Material): Triple<Double, Double, Double> = transaction {
			val maxPrice = SellOrders.price.max()
			val minPrice = SellOrders.price.min()
			val avgPrice = SellOrders.price.avg()

			SellOrders
				.slice(maxPrice, minPrice, avgPrice)
				.select { SellOrders.material eq material.name }
				.first()
				.let {
					Triple(it[maxPrice] ?: 1.0, it[minPrice] ?: 1.0, it[avgPrice]?.toDouble() ?: 1.0)
				}
		}
	}

	data class Order(
		val material: Material,
		val amount: Int,
		val maximumPrice: Double,
		val minimumPrice: Double,
		val averagePrice: Double,
	)

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

	val material: Material
		get() = Material.getMaterial(row[SellOrders.material]) ?: DEFAULT_MATERIAL

	val price: Double
		get() = row[SellOrders.price]

	val amount: Int
		get() = row[SellOrders.amount]
}