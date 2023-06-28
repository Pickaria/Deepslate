package fr.pickaria.controller.reforge

import fr.pickaria.controller.artefact.artefact
import fr.pickaria.model.reforge.ReforgeLevel
import fr.pickaria.model.reforge.rarityNamespace
import fr.pickaria.model.reforge.reforgeConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.text.DecimalFormat


private val formatter = DecimalFormat("#.##")
private val percentFormatter = DecimalFormat("#.##%").apply { positivePrefix = "+" }


/**
 * Returns the rarity corresponding to the ItemStack.
 */
var ItemStack.artefactRarity: ReforgeLevel
	get() = reforgeConfig.levels[itemMeta.persistentDataContainer.get(rarityNamespace, PersistentDataType.STRING)]
		?: reforgeConfig.levels[reforgeConfig.defaultLevel]!!
	set(value) {
		editMeta {
			it.persistentDataContainer.set(rarityNamespace, PersistentDataType.STRING, value.key)
		}
	}

/**
 * Updates the display name and the lore of the ItemStack to better correspond to the item's rarity.
 */
fun ItemStack.updateLore(): ItemStack {
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
		}.toComponent()

		it.displayName(newDisplayName)

		val newLore: MutableList<Component?> = mutableListOf(
			Component.empty(),
			MiniMessage(rarity.color) { "name" to rarity.displayName }.toComponent().decoration(
				TextDecoration.ITALIC,
				TextDecoration.State.FALSE
			),
			artefact?.label?.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)

		if (it.hasAttributeModifiers()) {
			val defaultAttributes = mutableListOf<Component>()
			val customAttributes = mutableListOf<Component>()

			it.attributeModifiers?.forEach { attribute, modifier ->
				if (modifier.operation == AttributeModifier.Operation.ADD_NUMBER && modifier.amount != 0.0 || modifier.operation != AttributeModifier.Operation.ADD_NUMBER) {
					if (modifier.name == "default") {
						defaultAttributes.add(attributeLore(attribute, modifier, type.equipmentSlot))
					} else {
						customAttributes.add(attributeLore(attribute, modifier, type.equipmentSlot))
					}
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

private fun attributeLore(attribute: Attribute, modifier: AttributeModifier, slot: EquipmentSlot): Component {
	val amount = if (modifier.operation == AttributeModifier.Operation.ADD_NUMBER) {
		val formatted = if (attribute == Attribute.GENERIC_ATTACK_SPEED) {
			formatter.format(4 + modifier.amount)
		} else {
			formatter.format(modifier.amount)
		}

		val base = if (slot.isHand) {
			Component.space()
		} else {
			Component.text('+')
		}

		base.append(Component.text(formatted))
	} else {
		Component.text(percentFormatter.format(modifier.amount))
	}

	val color =
		if (modifier.name == "default" && modifier.operation == AttributeModifier.Operation.ADD_NUMBER && slot == EquipmentSlot.HAND) {
			NamedTextColor.DARK_GREEN
		} else if (modifier.amount >= 0) {
			NamedTextColor.BLUE
		} else {
			NamedTextColor.RED
		}

	val name = Component.translatable(attribute.translationKey())
	return amount.appendSpace().append(name).color(color).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
}