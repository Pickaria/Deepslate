package fr.pickaria.model.economy

import kotlinx.serialization.Serializable
import org.bukkit.Material

@Serializable
data class PhysicalCurrency(
	val value: Double,
	val material: Material,
)
