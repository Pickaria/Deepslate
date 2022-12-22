package fr.pickaria.shops

import fr.pickaria.menuNamespace
import fr.pickaria.shared.ConfigProvider
import fr.pickaria.spawner.spawnVillager
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.persistence.PersistentDataType
import java.util.*

class VillagerConfig : ConfigProvider() {
	val name: Component by this
	val type: Villager.Type by this
	val profession: Villager.Profession by this
	val uuid: UUID by this

	private val shop: ShopType
		get() = ShopType.valueOf(section!!.name.uppercase())

	private val offers
		get() = when (shop) {
			ShopType.ARTEFACTS -> getArtefactsOffers()
			ShopType.BANK -> getBankOffers()
			ShopType.POTIONS -> getPotionsOffers()
			ShopType.REWARDS -> getRewardsOffers()
			else -> emptyList()
		}

	private val menu
		get() = when (shop) {
			ShopType.MARKET -> "market"
			ShopType.JOB -> "job"
			else -> null
		}

	fun create(location: Location): Villager {
		val villager = spawnVillager(location, name.examinableName(), uuid)

		Villager.Profession.BUTCHER
		Villager.Type.PLAINS

		villager.profession = profession
		villager.villagerType = type
		villager.customName(name)
		villager.isCustomNameVisible = true

		val merchant = villager as Merchant
		merchant.recipes = offers

		menu?.let {
			villager.persistentDataContainer.set(menuNamespace, PersistentDataType.STRING, it)
		}

		return villager
	}
}