package fr.pickaria

import fr.pickaria.artefact.*
import fr.pickaria.reforge.canBeEnchanted
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.text.DecimalFormat


private val formatter = DecimalFormat("#.##")
private val percentFormatter = DecimalFormat("#.##%").apply { positivePrefix = "+" }


/**
 * Returns the rarity corresponding to the ItemStack.
 */
val ItemStack.artefactRarity: Rarity
	get() {
		val (level, hasCustomAttribute) = getRarityLevel()
		for (rarity in Config.sortedRarities) {
			if (rarity.attributes <= level) {
				return rarity
			}
		}
		return Config.lowestRarity
	}

/**
 * Sums the amount of all attribute modifiers.
 */
fun ItemStack.getRarityLevel(): Pair<Int, Boolean> {
	var attributeLevel = if (isArtefact()) 1 else 0
	var hasCustomAttributes = false

	itemMeta.attributeModifiers?.forEach { _, modifier ->
		if (modifier.name == "custom") {
			attributeLevel += if (modifier.amount >= 0.0) 1 else -1
			hasCustomAttributes = true
		}
	}

	if (hasCustomAttributes && attributeLevel == 0) {
		attributeLevel = 1
	}

	return attributeLevel to hasCustomAttributes
}

/**
 * Updates the display name and the lore of the ItemStack to better correspond to the item's rarity.
 */
fun ItemStack.updateRarity(): ItemStack {
	if (!type.canBeEnchanted) return this
	val rarity = artefactRarity

	editMeta {
		it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

		val displayName: Component = it.displayName()?.let { displayName ->
			if (displayName is TranslatableComponent) {
				Component.translatable(type.translationKey())
			} else {
				val newChildren = displayName.children().map { child ->
					child.style(Style.empty())
				}
				displayName.compact().children(newChildren).style(Style.empty())
			}
		} ?: Component.translatable(type.translationKey())

		val newDisplayName = MiniMessage(rarity.color) {
			"name" to displayName
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				.decorate(TextDecoration.BOLD)
		}.message

		it.displayName(newDisplayName)

		val newLore: MutableList<Component?> = mutableListOf(
			Component.empty(),
			MiniMessage(rarity.color) { "name" to rarity.name }.message.decoration(
				TextDecoration.ITALIC,
				TextDecoration.State.FALSE
			),
			artefact?.label?.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)

		if (it.hasAttributeModifiers()) {
			val defaultAttributes = mutableListOf<Component>()
			val customAttributes = mutableListOf<Component>()

			it.attributeModifiers?.forEach { attribute, modifier ->
				if (modifier.name == "default") {
					defaultAttributes.add(attributeLore(attribute, modifier))
				} else {
					customAttributes.add(attributeLore(attribute, modifier))
				}
			}

			if (defaultAttributes.isNotEmpty()) {
				newLore.add(Component.empty())
				newLore.add(
					Component.text("Statistiques de base :", NamedTextColor.GRAY)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				)
				newLore.addAll(defaultAttributes)
			}

			if (customAttributes.isNotEmpty()) {
				newLore.add(Component.empty())
				newLore.add(
					Component.text("Statistiques améliorées :", NamedTextColor.GRAY)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				)
				newLore.addAll(customAttributes)
			}
		}

		it.lore(newLore.filterNotNull())
	}

	return this
}

private fun attributeLore(attribute: Attribute, modifier: AttributeModifier): Component {
	val amount = when (modifier.operation) {
		AttributeModifier.Operation.ADD_NUMBER -> {
			val component = if (attribute == Attribute.GENERIC_ATTACK_SPEED) {
				Component.text(formatter.format(4 + modifier.amount))
			} else {
				Component.text(formatter.format(modifier.amount))
			}

			Component.space().append(component)
		}

		AttributeModifier.Operation.ADD_SCALAR -> Component.text(percentFormatter.format(modifier.amount))
		AttributeModifier.Operation.MULTIPLY_SCALAR_1 -> Component.text(percentFormatter.format(modifier.amount))
	}

	val color = if (modifier.name == "default") {
		NamedTextColor.DARK_GREEN
	} else if (modifier.amount >= 0) {
		NamedTextColor.BLUE
	} else {
		NamedTextColor.RED
	}

	val name = Component.translatable(attribute.translationKey())
	return amount.appendSpace().append(name).color(color).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
}