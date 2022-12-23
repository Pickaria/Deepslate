package fr.pickaria.controller.shop

import fr.pickaria.model.shop.ShopType
import fr.pickaria.model.shop.VillagerConfig
import fr.pickaria.model.shop.menuNamespace
import fr.pickaria.spawner.spawnVillager
import org.bukkit.Location
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.persistence.PersistentDataType

class VillagerController(val model: VillagerConfig) {
	private val offers
		get() = when (model.type) {
			ShopType.ARTEFACTS -> getArtefactsOffers()
			ShopType.BANK -> getBankOffers()
			/*ShopType.POTIONS -> getPotionsOffers()
			ShopType.REWARDS -> getRewardsOffers()*/
			else -> emptyList()
		}

	private val menu
		get() = when (model.type) {
			ShopType.MARKET -> "market"
			ShopType.JOB -> "job"
			else -> null
		}

	fun create(location: Location): Villager {
		val villager = spawnVillager(location, model.name.examinableName(), model.uuid)

		Villager.Profession.BUTCHER
		Villager.Type.PLAINS

		villager.profession = model.profession
		villager.villagerType = model.villagerType
		villager.customName(model.name)
		villager.isCustomNameVisible = true

		val merchant = villager as Merchant
		merchant.recipes = offers

		menu?.let {
			villager.persistentDataContainer.set(menuNamespace, PersistentDataType.STRING, it)
		}

		return villager
	}
}