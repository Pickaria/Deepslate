package fr.pickaria.model.premium

import fr.pickaria.model.config
import kotlinx.serialization.Serializable

@Serializable
data class PremiumConfig(
	val ranks: Map<String, Rank>,
)

val premiumConfig = config<PremiumConfig>("premium.yml")
