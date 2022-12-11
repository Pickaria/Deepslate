package fr.pickaria.reforge

import fr.pickaria.artefact.Config
import fr.pickaria.reforgeNamespace
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.random.Random


fun ItemStack.addDefaultAttributes() {
	editMeta {
		// Clear all previous attributes
		it.removeAttributeModifier(type.equipmentSlot)

		// Re-add default attributes
		type.getDefaultAttributeModifiers(type.equipmentSlot).forEach { attribute, modifier ->
			val newModifier = AttributeModifier(
				UUID.randomUUID(),
				"default",
				modifier.amount,
				modifier.operation,
				modifier.slot
			)

			it.addAttributeModifier(attribute, newModifier)
		}
	}
}

fun ItemStack.addRandomAttribute(attribute: Attribute) {
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
