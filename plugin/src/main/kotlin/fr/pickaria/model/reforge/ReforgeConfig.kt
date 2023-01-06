package fr.pickaria.model.reforge

import fr.pickaria.model.config
import fr.pickaria.model.serializers.SoundSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound


@Serializable
data class ReforgeConfig(
	val levels: Map<String, ReforgeLevel>,

	@SerialName("charged_lapis_description")
	val chargedLapisDescription: List<String>,

	@SerialName("charged_lapis_name")
	val chargedLapisName: String,

	@SerialName("default_level")
	val defaultLevel: String,

	@Serializable(with = SoundSerializer::class)
	@SerialName("enchant_sound")
	val enchantSound: Sound,
)

val reforgeConfig = config<ReforgeConfig>("reforge.yml")
