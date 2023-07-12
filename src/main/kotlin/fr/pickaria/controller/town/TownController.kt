package fr.pickaria.controller.town

import fr.pickaria.model.town.Resident
import fr.pickaria.model.town.Residents
import fr.pickaria.model.town.Town
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

val Player.resident: Resident?
	get() = transaction {
		Resident.find {
			Residents.playerUuid eq this@resident.uniqueId
		}.firstOrNull()
	}

val Player.town: Town?
	get() = resident?.town
