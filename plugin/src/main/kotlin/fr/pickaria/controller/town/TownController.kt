package fr.pickaria.controller.town

import fr.pickaria.model.town.Town
import fr.pickaria.model.town.Towns
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.transactions.transaction

class TownController(private val model: Town) {
	companion object {
		fun count() = transaction {
			Town.count()
		}

		fun all(limit: Int, offset: Long) = transaction {
			Town.all().limit(limit, offset).map {
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

		operator fun invoke(identifier: String, flag: ItemStack) = transaction {
			Town.new {
				this.identifier = identifier
				this.flag = flag
			}.let {
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

	var flag: ItemStack
		get() = model.flag
		set(value) {
			model.flag = value
		}
}
