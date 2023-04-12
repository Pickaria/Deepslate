package fr.pickaria.model.reforge

import fr.pickaria.model.serializers.AdvancementSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.advancement.Advancement

@Serializable
data class ReforgeLevel(
	val key: String,

	@SerialName("display_name")
	val displayName: String,

	@SerialName("color")
	val color: String,

	@SerialName("minimum_level")
	val minimumLevel: Int,

	@SerialName("maximum_level")
	val maximumLevel: Int,

	@SerialName("minimum_attributes")
	val minimumAttributes: Int = 0,

	@SerialName("maximum_attributes")
	val maximumAttributes: Int = 0,

	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement? = null,

	val attributes: List<ReforgeAttribute> = listOf(),
)
