package fr.pickaria.model.market

import fr.pickaria.model.config
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketConfig(
	@SerialName("sell_percentage")
	val sellPercentage: Double,
	@SerialName("buy_percentage")
	val buyPercentage: Double,
	@SerialName("minimum_price")
	val minimumPrice: Double,
)

val marketConfig = config<MarketConfig>("market.yml")
