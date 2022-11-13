package fr.pickaria.economy

import fr.pickaria.shared.GlowEnchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun ItemStack.isCurrency(): Boolean =
	itemMeta?.let {
		val hasCurrency = with(it.persistentDataContainer) {
			has(currencyNamespace, PersistentDataType.STRING) && has(valueNamespace, PersistentDataType.DOUBLE)
		}
		val isEnchanted = it.hasEnchants() && it.enchants.contains(GlowEnchantment.instance)
		hasCurrency && isEnchanted
	} ?: false

val ItemStack.account: String?
	get() = if (isCurrency()) {
		itemMeta?.persistentDataContainer?.get(currencyNamespace, PersistentDataType.STRING)
	} else {
		null
	}

val ItemStack.currency: Currency?
	get() = account?.let { currencies[it] }

val ItemStack.value: Double
	get() = itemMeta?.persistentDataContainer?.get(valueNamespace, PersistentDataType.DOUBLE) ?: 0.0

val ItemStack.totalValue: Double
	get() = amount * value
