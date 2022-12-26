package fr.pickaria.controller.town

import fr.pickaria.model.town.*
import fr.pickaria.shared.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

val OfflinePlayer.town: TownController?
	get() = transaction {
		Resident.find {
			Residents.playerUuid eq this@town.uniqueId
		}.firstOrNull()?.let {
			TownController(it.town)
		}
	}

fun OfflinePlayer.hasTown() = transaction {
	Resident.find {
		Residents.playerUuid eq this@hasTown.uniqueId
	}.firstOrNull() != null
}

val Player.resident: Resident?
	get() = transaction {
		Resident.find {
			Residents.playerUuid eq this@resident.uniqueId
		}.firstOrNull()
	}

val OfflinePlayer.residentRank: ResidentRank?
	get() = transaction {
		Resident.find {
			Residents.playerUuid eq this@residentRank.uniqueId
		}.firstOrNull()?.rank
	}

fun OfflinePlayer.createResident(town: TownController, rank: ResidentRank = ResidentRank.CITIZEN) =
	transaction {
		Resident.new {
			player = this@createResident
			this.town = town.model
			this.rank = rank
		}
	}

fun OfflinePlayer.joinTown(town: TownController, rank: ResidentRank = ResidentRank.CITIZEN) = transaction {
	Resident.find {
		Residents.playerUuid eq this@joinTown.uniqueId
	}.firstOrNull()?.apply {
		this.town = town.model
		this.rank = rank
	}
} ?: createResident(town, rank)

fun OfflinePlayer.leaveTown(town: TownController) {
	transaction {
		Resident.find {
			(Residents.playerUuid eq this@leaveTown.uniqueId) and (Residents.townId eq town.model.id)
		}.firstOrNull()?.delete()
	}

	if (town.residentCount == 0L) {
		val message = MiniMessage(townConfig.townEmptyDeleted) {
			"town" to town.identifier
		}.message
		Bukkit.broadcast(message)
		town.delete()
	}
}

fun OfflinePlayer.hasTownPermission(town: TownController, permission: TownPermission): Boolean {
	// TODO
	return true
}
