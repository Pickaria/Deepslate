package fr.pickaria.model.market

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

internal object Orders : Table() {
	val id = integer("id").autoIncrement()
	val seller = uuid("seller")
	val material = varchar("material", 256)
	val price = double("price").default(0.0)
	val amount = integer("amount").default(1)

	override val primaryKey = PrimaryKey(id)
}

internal val DEFAULT_MATERIAL = Material.BARRIER

class Order private constructor(private val row: ResultRow) {
	companion object {
		fun create(
			seller: OfflinePlayer,
			material: Material,
			amount: Int = 1,
			price: Double = 1.0
		): Order? = transaction {
			Orders.insert {
				it[Orders.seller] = seller.uniqueId
				it[Orders.material] = material.name
				it[Orders.price] = price
				it[Orders.amount] = amount
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
		 * Get one sell order by id.
		 */
		fun get(id: Int): Order? = transaction {
			Orders.select { Orders.id eq id }
				.firstOrNull()?.let {
					Order(it)
				}
		}

		/**
		 * Get all specific material's orders with given type.
		 */
		fun get(material: Material): List<Order> = transaction {
			Orders.select {
				(Orders.material eq material.name) and
						(Orders.amount greater 0)
			}.map {
				Order(it)
			}
		}

		fun getListings(limit: Int, offset: Long = 0): List<Listing> = transaction {
			val minPrice = Orders.price.min()
			val avgPrice = Orders.price.avg()
			val maxPrice = Orders.price.max()
			val sumAmount = Orders.amount.sum()

			Orders
				.slice(Orders.material, sumAmount, minPrice, avgPrice, maxPrice)
				.select {
					Orders.amount greater 0
				}
				.groupBy(Orders.material)
				.limit(limit, offset)
				.map {
					Listing(
						Material.getMaterial(it[Orders.material]) ?: DEFAULT_MATERIAL,
						it[sumAmount] ?: 0,
						it[minPrice]?.toDouble() ?: 0.0,
						it[avgPrice]?.toDouble() ?: 0.0,
						it[maxPrice]?.toDouble() ?: 0.0,
					)
				}
		}

		fun count(): Long = transaction {
			Orders
				.slice(Orders.material)
				.select {
					Orders.amount greater 0
				}
				.groupBy(Orders.material)
				.count()
		}

		fun count(seller: OfflinePlayer): Long = transaction {
			Orders
				.slice(Orders.seller)
				.select {
					(Orders.seller eq seller.uniqueId) and
							(Orders.amount greater 0)
				}
				.count()
		}

		fun getSumAmount(material: Material) = transaction {
			val sumAmount = Orders.amount.sum()

			Orders
				.slice(sumAmount)
				.select {
					Orders.material eq material.name
				}
				.groupBy(Orders.material)
				.firstOrNull()?.get(sumAmount) ?: 0
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
			val minPrice = Orders.price.min()
			val avgPrice = Orders.price.avg()
			val maxPrice = Orders.price.max()

			Orders
				.slice(maxPrice, minPrice, avgPrice)
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0)
				}
				.first()
				.let {
					Triple(it[minPrice] ?: 1.0, it[avgPrice]?.toDouble() ?: 1.0, it[maxPrice] ?: 1.0)
				}
		}

		fun getAveragePrice(material: Material): Double = transaction {
			val column = Orders.price.avg()

			Orders
				.slice(column)
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0)
				}
				.first()
				.let {
					it[column]?.toDouble() ?: 1.0
				}
		}

		fun findSellOrders(material: Material): List<Order> = transaction {
			Orders
				.select {
					(Orders.material eq material.name) and
							(Orders.amount greater 0)
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
					Orders.amount greater 0
				}
				.groupBy(Orders.material)
				.mapNotNull {
					Material.getMaterial(it[Orders.material])
				}
		}

		fun get(seller: OfflinePlayer, limit: Int, offset: Long = 0): List<Order> = transaction {
			Orders
				.select {
					Orders.seller eq seller.uniqueId
				}
				.limit(limit, offset)
				.map {
					Order(it)
				}
		}
	}

	data class Listing(
		val material: Material,
		val amount: Int,
		val minimumPrice: Double,
		val averagePrice: Double,
		val maximumPrice: Double,
	)

	private val whereClause = { Orders.id eq this.id }

	fun delete() = transaction {
		Orders.deleteWhere { whereClause() }
	}

	val id: Int
		get() = row[Orders.id]

	val seller: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(row[Orders.seller])

	val material: Material
		get() = Material.getMaterial(row[Orders.material]) ?: DEFAULT_MATERIAL

	val price: Double
		get() = row[Orders.price]

	var amount: Int
		get() = row[Orders.amount]
		set(value) = transaction {
			Orders.update({ whereClause() }) {
				it[amount] = value
			}
		}
}