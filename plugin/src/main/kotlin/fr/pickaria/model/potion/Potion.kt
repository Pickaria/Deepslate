package fr.pickaria.model.potion

import fr.pickaria.controller.potion.PotionController
import fr.pickaria.model.serializers.MiniMessageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

@Serializable
data class Potion(
	@Serializable(with = MiniMessageSerializer::class)
	val label: Component,
	val color: BossBar.Color,
	val duration: Int,
	val description: String,
	@SerialName("effect_name")
	val effectName: String,
	val power: Int,
	val type: PotionType,
)

fun Potion.toController(): PotionController = PotionController(this)
