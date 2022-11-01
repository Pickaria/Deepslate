package fr.pickaria.potion

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

fun createPotion(config: PotionConfig.Configuration, amount: Int): ItemStack {
	val itemStack = ItemStack(Material.POTION, amount)
	val potion = (itemStack.itemMeta as PotionMeta)

	potion.color = config.potionColor
	potion.persistentDataContainer.set(namespace, PersistentDataType.STRING, config.key)
	potion.addEnchant(Enchantment.MENDING, 1, true)
	potion.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS)

	potion.displayName(config.label)
	potion.lore(config.lore)

	itemStack.itemMeta = potion

	return itemStack
}