package fr.pickaria.model.town

import fr.pickaria.model.now
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Towns : IntIdTable() {
	val name = varchar("name", 32)
	val flag = binary("flag", 2048)
	val createdAt = datetime("created_at").clientDefault { now() }
	val isOpen = bool("is_open").default(false)
}

class Town(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Town>(Towns)

	var name by Towns.name
	var rawFlag by Towns.flag
	var isOpen by Towns.isOpen
	val createdAt by Towns.createdAt

	val members by Resident referrersOn Residents.town
}

var Town.flag: ItemStack
	get() = ItemStack.deserializeBytes(rawFlag)
	set(value) {
		rawFlag = value.serializeAsBytes()
	}