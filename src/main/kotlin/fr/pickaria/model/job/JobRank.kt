package fr.pickaria.model.job

import fr.pickaria.model.serializers.AdvancementSerializer
import kotlinx.serialization.Serializable
import org.bukkit.advancement.Advancement

@Serializable
data class JobRank(
	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement,

	val suffix: String? = null,
)
