package fr.pickaria.model.town

import fr.pickaria.model.now
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object Residents : IntIdTable() {
	val playerUuid = uuid("player_uuid").uniqueIndex()
	val townId = reference("town_id", Towns, onDelete = ReferenceOption.CASCADE)
	val memberSince = datetime("creation_date").clientDefault { now() }
	val rank = enumerationByName<ResidentRank>("rank", 8).default(ResidentRank.CITIZEN)
}

class Resident(id: EntityID<Int>) : IntEntity(id) {
	companion object : IntEntityClass<Resident>(Residents)

	var town by Town referencedOn Residents.townId
	private var playerUuid by Residents.playerUuid
	val memberSince by Residents.memberSince
	var rank by Residents.rank

	var player: OfflinePlayer
		get() = Bukkit.getOfflinePlayer(playerUuid)
		set(value) {
			playerUuid = value.uniqueId
		}
}
