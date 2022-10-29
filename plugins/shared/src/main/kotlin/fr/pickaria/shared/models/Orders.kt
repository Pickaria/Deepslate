package fr.pickaria.shared.models

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

enum class OrderType {
	BUY,
	SELL,
}

internal object Orders : Table() {
	val id = integer("id").autoIncrement()
	val seller = uuid("seller")
	val material = varchar("material", 256)
	val price = double("price").default(0.0)
	val amount = integer("amount").default(1)
	val type = enumeration<OrderType>("type")

	override val primaryKey = PrimaryKey(id)
}

internal val DEFAULT_MATERIAL = Material.BARRIER

class Order private constructor(private val row: ResultRow) {
	companion object {
		fun create(seller: OfflinePlayer, material: Material, orderType: OrderType, amount: Int = 1, price: Double = 1.0) = transaction {
			Orders.insert {
				it[this.seller] = seller.uniqueId
				it[this.material] = material.name
				it[this.price] = price
				it[this.amount] = amount
				it[this.type] = orderType
			}.resultedValues?.firstOrNull()
		}?.let {
			Order(it)
		}

		/**
		 * Get one sell order by id.
		 */
		fun get(id: Int) = transaction {
			Order { Orders.id eq id }
		}

		/**
		 * Get all specific material's sell orders.
		 */
		fun get(material: Material): List<Order> = transaction {
			Orders.select {
				Orders.material eq material.name
			}.map {
				Order(it)
			}
		}

		fun get(type: OrderType): List<Listing> = transaction {
			val maxPrice = Orders.price.max()
			val minPrice = Orders.price.min()
			val avgPrice = Orders.price.avg()
			val sumAmount = Orders.amount.sum()

			Orders
				.slice(Orders.material, sumAmount, maxPrice, minPrice, avgPrice)
				.select { Orders.type eq type }
				.groupBy(Orders.material)
				.map {
					Listing(
						Material.getMaterial(it[Orders.material]) ?: DEFAULT_MATERIAL,
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
			Orders.select {
				Orders.seller eq seller.uniqueId
			}.map {
				Order(it)
			}
		}

		/**
		 * Get maximum, minimum and average prices of sell orders by material.
		 */
		fun getPrices(material: Material): Triple<Double, Double, Double> = transaction {
			val maxPrice = Orders.price.max()
			val minPrice = Orders.price.min()
			val avgPrice = Orders.price.avg()

			Orders
				.slice(maxPrice, minPrice, avgPrice)
				.select { (Orders.material eq material.name) and (Orders.type eq OrderType.SELL) }
				.first()
				.let {
					Triple(it[maxPrice] ?: 1.0, it[minPrice] ?: 1.0, it[avgPrice]?.toDouble() ?: 1.0)
				}
		}
	}

	data class Listing(
		val material: Material,
		val amount: Int,
		val maximumPrice: Double,
		val minimumPrice: Double,
		val averagePrice: Double,
	)

	private constructor(whereClause: SqlExpressionBuilder.() -> Op<Boolean>) : this(
		transaction {
			Orders.select {
				whereClause()
			}.first()
		}
	)

	private val whereClause = { Orders.id eq this.id }

	val id: Int
		get() = row[Orders.id]

	val seller: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(row[Orders.seller])

	val material: Material
		get() = Material.getMaterial(row[Orders.material]) ?: DEFAULT_MATERIAL

	val price: Double
		get() = row[Orders.price]

	val amount: Int
		get() = row[Orders.amount]
}