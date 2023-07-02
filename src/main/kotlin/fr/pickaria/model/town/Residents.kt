package fr.pickaria.model.town

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Residents : IntIdTable() {
	val playerUuid = uuid("player_uuid")
	val town = reference("town", Towns)

	init {
		index(true, playerUuid, town)
	}
}

class Resident(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Resident>(Residents)

	var playerUuid by Residents.playerUuid
	var town by Town referencedOn Residents.town
}
