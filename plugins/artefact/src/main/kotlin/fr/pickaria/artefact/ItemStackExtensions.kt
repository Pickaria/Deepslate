package fr.pickaria.artefact

import fr.pickaria.artefactNamespace
import fr.pickaria.receptacleNamespace
import fr.pickaria.reforgeNamespace
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * Returns the artefact of the item if any.
 */
val ItemStack.artefact
	get(): Artefact? =
		itemMeta?.persistentDataContainer?.get(artefactNamespace, PersistentDataType.STRING)?.let {
			try {
				Config.artefacts[it]
			} catch (_: IllegalArgumentException) {
				null
			}
		}

/**
 * Returns the artefact configuration of the item if any.
 */
@Deprecated("", ReplaceWith("getartefact"))
fun ItemStack.getArtefactConfig() = artefact

/**
 * Returns true if the given item is an artefact, it can be a receptacle as well.
 */
fun ItemStack.isArtefact(): Boolean = artefact != null

/**
 * Returns true if the given item is a receptacle.
 */
fun ItemStack.isReceptacle(): Boolean =
	itemMeta?.persistentDataContainer?.get(receptacleNamespace, PersistentDataType.BYTE) != null

/**
 * Sets the artefact on the ItemStack.
 */
fun ItemStack.setArtefact(artefact: Artefact): ItemStack {
	editMeta {
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.persistentDataContainer.set(artefactNamespace, PersistentDataType.STRING, artefact.section!!.name)
	}

	return this
}

/**
 * Returns the rarity corresponding to the ItemStack.
 */
val ItemStack.artefactRarity: Rarity
	get() {
		val artefactScore = if (isArtefact()) 1 else 0
		val level = getRarityLevel() + artefactScore
		for (rarity in Config.sortedRarities) {
			if (rarity.attributes <= level) {
				return rarity
			}
		}
		return Config.lowestRarity
	}

/**
 * Updates the display name and the lore of the ItemStack to better correspond to the item's rarity.
 */
fun ItemStack.updateRarity(): ItemStack {
	val rarity = artefactRarity

	editMeta {
		val displayName: Component = it.displayName()?.let { displayName ->
			val newChildren = displayName.children().map { child ->
				child.style(Style.empty())
			}
			displayName.compact().children(newChildren).style(Style.empty())
		} ?: Component.translatable(type.translationKey())

		val newDisplayName = MiniMessage(rarity.color) {
			"name" to displayName
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				.decorate(TextDecoration.BOLD)
		}.message

		it.displayName(newDisplayName)

		val newLore: List<Component> = listOfNotNull(
			Component.empty(),
			MiniMessage(rarity.color) { "name" to rarity.name }.message.decoration(
				TextDecoration.ITALIC,
				TextDecoration.State.FALSE
			),
			artefact?.label?.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)

		it.lore(newLore)
	}

	return this
}

/**
 * Returns true if the ItemStack is the item that can change stats in enchanting table.
 */
fun ItemStack.isAttributeItem() =
	type == Material.LAPIS_LAZULI && itemMeta.persistentDataContainer.has(reforgeNamespace)

/**
 * Sums the amount of all attribute modifiers.
 */
fun ItemStack.getRarityLevel(): Double {
	val amount = itemMeta.attributeModifiers?.size()?.toDouble() ?: 0.0
	var attributeLevel = 0.0

	itemMeta.attributeModifiers?.forEach { _, modifier ->
		attributeLevel += modifier.amount * 10
	}

	val artefactLevel = if (isArtefact()) amount else 0.0

	return attributeLevel + artefactLevel
}