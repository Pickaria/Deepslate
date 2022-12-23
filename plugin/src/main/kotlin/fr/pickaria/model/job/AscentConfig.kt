package fr.pickaria.model.job

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AscentConfig(
	@SerialName("start_level")
	val startLevel: Int,
	@SerialName("point_every")
	val pointEvery: Int,
	@SerialName("point_amount")
	val pointAmount: Int,
	@SerialName("experience_increase")
	val experienceIncrease: Double,
	@SerialName("money_increase")
	val moneyIncrease: Double,
)