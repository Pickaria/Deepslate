package fr.pickaria.model.economy

import fr.pickaria.model.serializers.AdvancementSerializer
import kotlinx.serialization.Serializable
import org.bukkit.advancement.Advancement

@Serializable
data class CurrencyAdvancement(
	val amount: Int,

	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement,
)
