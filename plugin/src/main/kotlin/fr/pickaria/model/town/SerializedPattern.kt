package fr.pickaria.model.town

import fr.pickaria.model.serializers.PatternTypeSerializer
import kotlinx.serialization.Serializable
import org.bukkit.DyeColor
import org.bukkit.block.banner.Pattern
import org.bukkit.block.banner.PatternType


@Serializable
data class SerializedPattern(
	val color: DyeColor,

	@Serializable(with = PatternTypeSerializer::class)
	val pattern: PatternType,
)

fun SerializedPattern.toPattern() = Pattern(color, pattern)
