package fr.pickaria.model.potion

import fr.pickaria.model.config
import kotlinx.serialization.Serializable

@Serializable
data class PotionConfig(
	val potions: Map<String, Potion>
)

val potionConfig = config<PotionConfig>("potion.yml")
