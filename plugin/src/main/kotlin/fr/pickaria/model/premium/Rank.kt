package fr.pickaria.model.premium

import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.Material

@Serializable
data class Rank(
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,
	val permission: String,
	val duration: Long,
	val price: Int,
	val description: List<String>,
	val material: Material,
)
