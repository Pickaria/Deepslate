package fr.pickaria.model.reforge

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.EnchantmentTarget


@Serializable
data class ReforgeAttribute(
	@SerialName("random_type")
	val randomType: RandomType,

	val attribute: Attribute,
	val minimum: Double,
	val maximum: Double,
	val operation: AttributeModifier.Operation,
	val target: EnchantmentTarget = EnchantmentTarget.ALL,
)