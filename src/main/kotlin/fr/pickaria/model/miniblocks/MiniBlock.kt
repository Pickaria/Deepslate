package fr.pickaria.model.miniblocks

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class MiniBlock(
	val material: Material,
	val texture: String,
	val price: Double? = null,
)
