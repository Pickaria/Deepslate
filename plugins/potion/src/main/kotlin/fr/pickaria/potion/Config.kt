package fr.pickaria.potion

import fr.pickaria.shared.ConfigProvider

object Config: ConfigProvider() {
	val potions by sectionLoader<PotionConfig>()
}