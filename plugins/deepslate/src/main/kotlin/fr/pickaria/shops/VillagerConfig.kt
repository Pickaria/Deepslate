package fr.pickaria.shops

import fr.pickaria.shared.ConfigProvider
import fr.pickaria.spawner.spawnVillager
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.entity.Villager
import org.bukkit.inventory.Merchant
import org.bukkit.inventory.MerchantRecipe
import java.util.*

class VillagerConfig : ConfigProvider() {
	val position: List<Int> by this
	val name: Component by this
	val type: Villager.Type by this
	val profession: Villager.Profession by this
	val uuid: UUID by this

	fun create(location: Location, offers: List<MerchantRecipe>): Villager {
		val villager = spawnVillager(location, name.examinableName(), uuid)

		Villager.Profession.BUTCHER
		Villager.Type.PLAINS

		villager.profession = profession
		villager.villagerType = type
		villager.customName(name)
		villager.isCustomNameVisible = true

		val merchant = villager as Merchant
		merchant.recipes = offers

		return villager
	}
}