package fr.pickaria.model.town

import fr.pickaria.model.now
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Towns : IntIdTable() {
	var identifier = varchar("town_id", 32).uniqueIndex()
	var flag = binary("flag", 1024)
	val creationDate = datetime("creation_date").clientDefault { now() }
	var balance = double("balance").default(0.0)
}

class Town(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Town>(Towns)

	var identifier by Towns.identifier
	private var flagByteArray by Towns.flag
	val creationDate by Towns.creationDate
	var balance by Towns.balance
	val residents by Resident referrersOn Residents.townId

	var flag: ItemStack
		get() = ItemStack.deserializeBytes(flagByteArray)
		set(value) {
			flagByteArray = value.serializeAsBytes()
		}
}
