package fr.pickaria.artefact

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Material

enum class Artefact(
	val material: Material,
	val displayName: Component,
	val lore: List<Component>,
	val color: Color? = null,
) {
	FIRE_BOOTS(
		Material.LEATHER_BOOTS,
		Component.text("Bottes de feu", NamedTextColor.RED)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Fait apparaitre des flammes", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("lorsque son porteur se déplace.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		Color.RED,
	),

	STEALTH_CHESTPLATE(
		Material.LEATHER_CHESTPLATE,
		Component.text("Plastron furtif", NamedTextColor.DARK_PURPLE)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Empêche le porteur de se faire", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("repérer par les monstres.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		Color.BLACK,
	),

	ICE_LEGGINGS(
		Material.LEATHER_LEGGINGS,
		Component.text("Pantalon de glace", NamedTextColor.AQUA)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Gèle les attaquants.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		Color.AQUA,
	),

	LUCKY_PICKAXE(
		Material.DIAMOND_PICKAXE,
		Component.text("Pioche chanceuse", NamedTextColor.GOLD)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("À une chance faible de faire apparaitre", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("un minerai en plus à chaque bloc cassé.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
	),
}