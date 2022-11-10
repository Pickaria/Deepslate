package fr.pickaria.database.models

import fr.pickaria.database.BukkitLogger
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
		fun create(
			seller: OfflinePlayer,
			material: Material,
			orderType: OrderType,
			amount: Int = 1,
			price: Double = 1.0
		): Order? = transaction {
			Orders.insert {
				it[Orders.seller] = seller.uniqueId
				it[Orders.material] = material.name
				it[Orders.price] = price
				it[Orders.amount] = amount
				it[type] = orderType
			}.resultedValues?.firstOrNull()
		}?.let {
			Order(it)
		}

		/**
		 * Get one sell order by id.
		 */
		fun get(seller: OfflinePlayer, id: Int): Order? = transaction {
			Orders.select { (Orders.id eq id) and (Orders.seller eq seller.uniqueId) }
				.firstOrNull()?.let {
					Order(it)
				}
		}

		/**
		 * Get all specific material's orders.
		 */
		fun get(material: Material): List<Order> = transaction {
			Orders.select {
				Orders.material eq material.name
			}.map {
				Order(it)
			}
		}

		/**
		 * Get all specific material's orders with given type.
		 */
		fun get(material: Material, type: OrderType): List<Order> = transaction {
			Orders.select {
				(Orders.material eq material.name) and
						(Orders.amount greater 0) and
						(Orders.type eq type)
			}.map {
				Order(it)
			}
		}

		fun getListings(type: OrderType, limit: Int, offset: Long = 0): List<Listing> = transaction {
			val avgPrice = Orders.price.avg()
			val sumAmount = Orders.amount.sum()
			addLogger(BukkitLogger)

			Orders
				.slice(Orders.material, sumAmount, avgPrice)
				.select {
					(Orders.type eq type) and
							(Orders.amount greater 0)
				}
				.groupBy(Orders.material)
				.limit(limit, offset)
				.map {
					Listing(
						Material.getMaterial(it[Orders.material]) ?: DEFAULT_MATERIAL,
						it[sumAmount] ?: 0,
						it[avgPrice]?.toDouble() ?: 0.0,
					)
				}
		}

		fun count(type: OrderType): Long = transaction {
			addLogger(BukkitLogger)

			Orders
				.slice(Orders.material)
				.select {
					(Orders.type eq type) and
							(Orders.amount greater 0)
				}
				.groupBy(Orders.material)
				.count()
		}

		/**
		 * Get all sell orders of a player.
		 */
		fun get(seller: OfflinePlayer): List<Order> = transaction {
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
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.SELL)
				}
				.first()
				.let {
					Triple(it[maxPrice] ?: 1.0, it[minPrice] ?: 1.0, it[avgPrice]?.toDouble() ?: 1.0)
				}
		}

		fun getAveragePrice(material: Material): Double = transaction {
			val column = Orders.price.avg()

			Orders
				.slice(column)
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.SELL)
				}
				.first()
				.let {
					it[column]?.toDouble() ?: 1.0
				}
		}

		fun getMinimumPrice(material: Material): Double = transaction {
			val column = Orders.price.min()

			Orders
				.slice(column)
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.SELL)
				}
				.first()
				.let {
					it[column]?.toDouble() ?: 1.0
				}
		}

		fun getMaximumPrice(material: Material): Double = transaction {
			val column = Orders.price.max()

			Orders
				.slice(column)
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.SELL)
				}
				.first()
				.let {
					it[column]?.toDouble() ?: 1.0
				}
		}

		/**
		 * Find buy orders with a given minimum price, sorted by cheaper last.
		 */
		fun findBuyOrders(material: Material, minimumPrice: Double): List<Order> = transaction {
			Orders
				.select {
					(Orders.material eq material.name) and
							(Orders.price greaterEq minimumPrice) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.BUY)
				}
				.orderBy(Orders.price, SortOrder.DESC)
				.map { Order(it) }
		}

		/**
		 * Find sell orders with a given maximum price, sorted by cheaper last.
		 */
		fun findSellOrders(material: Material, maximumPrice: Double): List<Order> = transaction {
			Orders
				.select {
					(Orders.material eq material.name) and
							(Orders.price lessEq maximumPrice) and
							(Orders.amount greater 0) and
							(Orders.type eq OrderType.SELL)
				}
				.orderBy(Orders.price, SortOrder.ASC)
				.map { Order(it) }
		}

		/**
		 * Get all materials currently being sold.
		 */
		fun getMaterials(): List<Material> = transaction {
			Orders.slice(Orders.material)
				.select {
					(Orders.type eq OrderType.SELL) and
							(Orders.amount greater 0)
				}
				.groupBy(Orders.material)
				.mapNotNull {
					Material.getMaterial(it[Orders.material])
				}
		}
	}

	data class Listing(
		val material: Material,
		val amount: Int,
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

	fun delete() {
		transaction {
			Orders.deleteWhere { whereClause() }
		}
	}

	val id: Int
		get() = row[Orders.id]

	val seller: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(row[Orders.seller])

	val material: Material
		get() = Material.getMaterial(row[Orders.material]) ?: DEFAULT_MATERIAL

	val price: Double
		get() = row[Orders.price]

	val type: OrderType
		get() = row[Orders.type]

	var amount: Int
		get() = row[Orders.amount]
		set(value) = transaction {
			Orders.update({ whereClause() }) {
				it[amount] = value
			}
		}
}