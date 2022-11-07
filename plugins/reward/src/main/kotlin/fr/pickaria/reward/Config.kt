package fr.pickaria.reward

import fr.pickaria.shared.ConfigProvider

class RewardConfig: ConfigProvider() {
	val lootTable by lootTableLoader
	val material by materialLoader
	val name by miniMessageDeserializer
	val price: Double by this
}

object Config: ConfigProvider() {
	val rewards by sectionLoader<RewardConfig>()
	val notEnoughMoney by miniMessageDeserializer
}