package fr.pickaria.model.reforge

import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component

@Serializable
data class Rarity(
	val color: String,
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,
	val attributes: Int,
)
