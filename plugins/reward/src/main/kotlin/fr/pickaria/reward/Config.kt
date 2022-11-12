package fr.pickaria.reward

import fr.pickaria.shared.ConfigProvider
import org.bukkit.Material
import org.bukkit.loot.LootTable

internal class RewardConfig: ConfigProvider() {
	val lootTable: LootTable by this
	val material: Material by this
	val name by miniMessageDeserializer
	val price: Double by this
	val purchasable: Boolean by this
}

internal object Config: ConfigProvider() {
	val rewards by sectionLoader<RewardConfig>()
	val notEnoughMoney by miniMessageDeserializer
	val cantPurchaseReward by miniMessageDeserializer
}