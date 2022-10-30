package fr.pickaria.market

import fr.pickaria.shared.ConfigProvider

object Config: ConfigProvider() {
	val sellPercentage: Double by this
	val buyPercentage: Double by this
	val minimumPrice: Double by this
}