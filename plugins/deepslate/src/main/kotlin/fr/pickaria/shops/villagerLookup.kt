package fr.pickaria.shops

import fr.pickaria.Config
import fr.pickaria.spawner.isCustomVillager
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer

fun villagerLookup() {
	Config.villagers.forEach { (_, config) ->
		getServer().getEntity(config.uuid)?.let { entity ->
			if (!isCustomVillager(entity)) {
				Bukkit.broadcastMessage("VILLAGER FOUND, REMOVING IT")
				entity.remove()
				config.create(entity.location)
			}
		}
	}
}