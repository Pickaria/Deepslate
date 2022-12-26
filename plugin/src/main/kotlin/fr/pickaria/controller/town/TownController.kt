package fr.pickaria.controller.town

import fr.pickaria.model.town.Resident
import fr.pickaria.model.town.ResidentRank
import fr.pickaria.model.town.Town
import fr.pickaria.model.town.Towns
import kotlinx.datetime.LocalDateTime
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class TownController(val model: Town) {
	companion object {
		/**
		 * @return The total amount of towns.
		 */
		fun count() = transaction {
			Town.count()
		}

		/**
		 * @return A list of all towns with given limit and offset.
		 */
		fun all(limit: Int, offset: Long = 0) = transaction {
			Town.all().limit(limit, offset).map {
				TownController(it)
			}
		}

		/**
		 * @return A list of all towns.
		 */
		fun all() = transaction {
			Town.all().map {
				TownController(it)
			}
		}

		operator fun get(id: Int) = transaction {
			Town.findById(id)?.let {
				TownController(it)
			}
		}

		operator fun get(identifier: String) = transaction {
			Town.find {
				Towns.identifier eq identifier
			}.firstOrNull()?.let {
				TownController(it)
			}
		}

		operator fun invoke(identifier: String, flag: ItemStack, mayor: OfflinePlayer) = transaction {
			Town.new {
				this.identifier = identifier
				this.flag = flag
			}.let {
				Resident.new {
					town = it
					player = mayor
					rank = ResidentRank.MAYOR
				}
				TownController(it)
			}
		}
	}

	fun delete() {
		transaction {
			model.delete()
		}
	}

	val id: Int
		get() = model.id.value

	val identifier: String
		get() = model.identifier

	var flag: ItemStack
		get() = model.flag
		set(value) {
			model.flag = value
		}

	fun residents(limit: Int, offset: Long = 0) = transaction {
		model.residents.limit(limit, offset).map {
			it.player
		}
	}

	fun residents() = transaction {
		model.residents.map {
			it.player
		}
	}

	val residentCount: Long
		get() = transaction { model.residents.count() }

	val balance: Double
		get() = transaction { model.balance }

	val creationDate: LocalDateTime
		get() = transaction { model.creationDate }

	fun deposit(amount: Double) = transaction {
		model.balance += amount
	}

	fun withdraw(amount: Double) = transaction {
		model.balance -= amount
	}
}
