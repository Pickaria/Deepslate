package fr.pickaria.model.reforge

import fr.pickaria.model.serializers.AdvancementSerializer
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.advancement.Advancement

@Serializable
data class Rarity(
	val color: String,
	@Serializable(with = MiniMessageSerializer::class)
	val name: Component,
	val attributes: Int,
	@Serializable(with = AdvancementSerializer::class)
	val advancement: Advancement? = null,
)
