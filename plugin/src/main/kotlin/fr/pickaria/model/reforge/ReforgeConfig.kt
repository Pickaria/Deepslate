package fr.pickaria.model.reforge

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.datasources.getResourceFileStream
import fr.pickaria.model.serializers.SoundSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.sound.Sound


@Serializable
data class ReforgeConfig(
	val rarities: Map<String, Rarity>,

	@SerialName("minimum_attribute")
	val minimumAttribute: Double,

	@SerialName("maximum_attribute")
	val maximumAttribute: Double,

	@SerialName("charged_lapis_description")
	val chargedLapisDescription: List<String>,

	@SerialName("charged_lapis_name")
	val chargedLapisName: String,

	@Serializable(with = SoundSerializer::class)
	@SerialName("enchant_sound")
	val enchantSound: Sound,
) {
	val lowestRarity by lazy {
		rarities.values.minByOrNull { it.attributes } ?: throw RuntimeException("Could not get default rarity.")
	}
	val sortedRarities by lazy {
		rarities.values.sortedByDescending { it.attributes }
	}
}

val reforgeConfig = Yaml.default.decodeFromStream<ReforgeConfig>(getResourceFileStream("reforge.yml"))
