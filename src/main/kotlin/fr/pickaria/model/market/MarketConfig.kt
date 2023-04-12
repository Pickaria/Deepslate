package fr.pickaria.model.market

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
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

val marketConfig = Yaml.default.decodeFromStream<MarketConfig>(getResourceFileStream("market.yml"))
