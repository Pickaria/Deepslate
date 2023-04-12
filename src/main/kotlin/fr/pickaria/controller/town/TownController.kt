package fr.pickaria.controller.town

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException
import com.palmergames.bukkit.towny.`object`.Resident
import com.palmergames.bukkit.towny.`object`.Town
import org.bukkit.entity.Player

val Player.resident: Resident?
	get() = TownyAPI.getInstance().getResident(this)

val Player.town: Town?
	get() = try {
		resident?.town
	} catch (_: NotRegisteredException) {
		null
	}
