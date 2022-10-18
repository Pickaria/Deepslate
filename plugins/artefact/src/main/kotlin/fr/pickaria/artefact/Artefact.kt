package fr.pickaria.artefact

import io.papermc.paper.inventory.ItemRarity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.enchantments.EnchantmentTarget

enum class Artefact(
	val displayName: Component,
	val lore: List<Component>,
	val target: EnchantmentTarget,
	val rarity: ItemRarity,
	val value: Int = 1,
	val color: Color? = null,
) {
	FLAME_COSMETICS(
		Component.text("Particules ardentes", NamedTextColor.RED)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Fait apparaitre des flammes", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("lorsque son porteur se déplace.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		EnchantmentTarget.ARMOR,
		ItemRarity.COMMON,
		1,
		Color.RED,
	),

	SNOWFLAKE_COSMETICS(
		Component.text("Particules glacées", NamedTextColor.RED)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Fait apparaitre des flocons", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("lorsque son porteur se déplace.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		EnchantmentTarget.ARMOR,
		ItemRarity.COMMON,
		1,
		Color.AQUA,
	),

	STEALTH(
		Component.text("Armure de furtivité", NamedTextColor.DARK_PURPLE)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Empêche le porteur de se faire", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("repérer par les monstres.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		EnchantmentTarget.ARMOR,
		ItemRarity.RARE,
		10,
		Color.BLACK,
	),

	ICE_THORNS(
		Component.text("Épines glacées", NamedTextColor.AQUA)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("Gèle les attaquants.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		EnchantmentTarget.ARMOR,
		ItemRarity.UNCOMMON,
		5,
		Color.AQUA,
	),

	LUCKY(
		Component.text("Chanceux", NamedTextColor.GOLD)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		listOf(
			Component.text("A faible chance de faire apparaitre", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
			Component.text("un minerai en plus à chaque bloc cassé.", NamedTextColor.GRAY)
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
		),
		EnchantmentTarget.TOOL,
		ItemRarity.EPIC,
		15,
	),
}