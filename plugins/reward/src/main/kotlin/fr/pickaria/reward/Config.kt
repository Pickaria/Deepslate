package fr.pickaria.reward

import fr.pickaria.shared.ConfigProvider
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.loot.LootTable

class RewardConfig : ConfigProvider() {
	val lootTable: LootTable by this
	val material: Material by this
	val name by miniMessageDeserializer
	val keys: Int by this
	val shards: Int by this
	val purchasable: Boolean by this
	val description: List<String> by this
}

object Config : ConfigProvider() {
	val rewards by sectionLoader<RewardConfig>()
	val notEnoughMoney by miniMessageDeserializer
	val cantPurchaseReward by miniMessageDeserializer
	val rewardCloseSound: Sound by this
	val rewardOpenSound: Sound by this
}