package fr.pickaria.model.rank

import fr.pickaria.model.config
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class RankConfig(
	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("owned_message")
	val ownedMessage: Component,

	@Serializable(with = MiniMessageSerializer::class)
	@SerialName("not_owned_message")
	val notOwnedMessage: Component,

	val ranks: Map<String, Rank>,
)

val rankConfig = config<RankConfig>("ranks.yml")
