package fr.pickaria.model.town

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.decodeFromStream
import fr.pickaria.model.getResourceFileStream
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class TownConfig(
	@SerialName("banner_price")
	val bannerPrice: Double,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_name_missing")
	val townNameMissing: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_name_exist")
	val townNameExist: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("town_not_created")
	val townNotCreated: Component,

	val page: String,
)

val townConfig = Yaml.default.decodeFromStream<TownConfig>(getResourceFileStream("town.yml"))
