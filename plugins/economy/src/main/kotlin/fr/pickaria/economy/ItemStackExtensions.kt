package fr.pickaria.economy

import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/**
 * The currency account of the current item if any.
 */
val ItemStack.account: String?
	get() = itemMeta?.persistentDataContainer?.get(currencyNamespace, PersistentDataType.STRING)

/**
 * The value of one item.
 */
val ItemStack.value: Double
	get() = itemMeta?.persistentDataContainer?.get(valueNamespace, PersistentDataType.DOUBLE) ?: 0.0

/**
 * The total value of the item stack.
 */
val ItemStack.totalValue: Double
	get() = amount * value