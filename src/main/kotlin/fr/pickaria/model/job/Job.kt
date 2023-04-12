package fr.pickaria.model.job

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class Job(
	val label: String,
	val icon: Material,
	val description: List<String>,
	@SerialName("experience_percentage")
	val experiencePercentage: Double,
	val multiplier: Int,
	@SerialName("revenue_increase")
	val revenueIncrease: Double,
	@SerialName("start_experience")
	val startExperience: Int,
	val type: JobType,
)
