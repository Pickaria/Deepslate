package fr.pickaria.model.potion

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
import kotlinx.serialization.Serializable

@Serializable
data class PotionConfig(
	val potions: Map<String, Potion>
)

val potionConfig = Yaml.default.decodeFromStream<PotionConfig>(getResourceFileStream("potion.yml"))
