package fr.pickaria.model.job

import fr.pickaria.model.serializers.AdvancementSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.advancement.Advancement

@Serializable
data class Job(
	val label: String,
	val icon: Material,
	val description: List<String>,
	val type: JobType,
	val multiplier: Int,

	@SerialName("experience_percentage")
	val experiencePercentage: Double,

	@SerialName("revenue_increase")
	val revenueIncrease: Double,

	@SerialName("start_experience")
	val startExperience: Int,

	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement,

	@SerialName("advancement_ascend")
	@Serializable(with = AdvancementSerializer::class)
	val advancementAscend: Advancement,

	@SerialName("advancement_max_level")
	@Serializable(with = AdvancementSerializer::class)
	val advancementMaxLevel: Advancement,
)
