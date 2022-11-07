package fr.pickaria.reward

import fr.pickaria.shared.ConfigProvider

class RewardConfig: ConfigProvider() {
	val lootTable by this.lootTableLoader
	val material by this.materialLoader
	val name by this.miniMessageDeserializer
}

object Config: ConfigProvider() {
	val rewards by this.sectionLoader<RewardConfig>()
	val otherKey: String by this
}