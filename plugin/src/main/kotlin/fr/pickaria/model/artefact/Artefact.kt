package fr.pickaria.model.artefact

import fr.pickaria.controller.artefact.ArtefactController
import fr.pickaria.model.serializers.MiniMessageSerializer
import io.papermc.paper.inventory.ItemRarity
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.EnchantmentTarget

@Serializable
data class Artefact(
	val type: ArtefactType,
	@Serializable(with = MiniMessageSerializer::class) val label: Component,
	val description: List<String>,
	val target: EnchantmentTarget,
	val rarity: ItemRarity,
	val value: Int,
)

fun Artefact.toController(): ArtefactController = ArtefactController(this)
