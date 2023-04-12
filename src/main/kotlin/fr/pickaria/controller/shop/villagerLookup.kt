package fr.pickaria.controller.shop

import fr.pickaria.model.shop.shopConfig
import fr.pickaria.model.shop.toController
import fr.pickaria.spawner.isCustomVillager
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer

fun villagerLookup() {
	shopConfig.villagers.forEach { (_, config) ->
		getServer().getEntity(config.uuid)?.let { entity ->
			if (!isCustomVillager(entity)) {
				Bukkit.broadcastMessage("VILLAGER FOUND, REMOVING IT")
				entity.remove()
				config.toController().create(entity.location)
			}
		}
	}
}