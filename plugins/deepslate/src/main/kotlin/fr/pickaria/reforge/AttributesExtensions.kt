package fr.pickaria.reforge

import fr.pickaria.Config
import fr.pickaria.reforgeNamespace
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random


/**
 * Clears all item's attributes.
 */
fun ItemStack.clearAttributes() {
	editMeta {
		it.removeAttributeModifier(type.equipmentSlot)
	}
}

/**
 * Updates the default attributes of the item with its enchantments or given enchants to add.
 */
fun ItemStack.updateDefaultAttributes(enchantsToAdd: Map<Enchantment, Int>? = null) {
	editMeta {
		// Clear all previous attributes
		if (it.hasAttributeModifiers()) {
			it.getAttributeModifiers(type.equipmentSlot).forEach { attribute, modifier ->
				if (modifier.name == "default") {
					it.removeAttributeModifier(attribute, modifier)
				}
			}
		}

		// Calculate enchantment damage increase
		val damageIncrease = (enchantsToAdd ?: enchantments).map { (enchantment, level) ->
			val increase = enchantment.getDamageIncrease(level, EntityCategory.NONE)
			increase
		}.sum()

		// Re-add default attributes
		type.getDefaultAttributeModifiers(type.equipmentSlot).forEach { attribute, modifier ->
			val amount = if (attribute == Attribute.GENERIC_ATTACK_DAMAGE) {
				modifier.amount + 1 + damageIncrease
			} else {
				modifier.amount
			}

			val newModifier = AttributeModifier(
				UUID.randomUUID(),
				"default",
				amount,
				modifier.operation,
				modifier.slot
			)

			it.addAttributeModifier(attribute, newModifier)
		}
	}
}

/**
 * Add an attribute with random modifier.
 */
fun ItemStack.addRandomAttributeModifier(attribute: Attribute) {
	editMeta {
		val amount = Random.nextDouble(Config.minimumAttribute, Config.maximumAttribute)
		val modifier = AttributeModifier(
			UUID.randomUUID(),
			"custom",
			amount,
			AttributeModifier.Operation.ADD_SCALAR,
			type.equipmentSlot
		)

		it.addAttributeModifier(
			attribute,
			modifier
		)
	}
}

/**
 * Returns true if the ItemStack is the item that can change stats in enchanting table.
 */
fun ItemStack.isAttributeItem() =
	type == Material.LAPIS_LAZULI && itemMeta.persistentDataContainer.has(reforgeNamespace)
