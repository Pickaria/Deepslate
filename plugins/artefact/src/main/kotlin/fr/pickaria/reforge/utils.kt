package fr.pickaria.reforge

import fr.pickaria.reforgeNamespace
import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun getAttributeItem(): ItemStack {
	val itemStack = ItemStack(Material.LAPIS_LAZULI)
	itemStack.editMeta {
		it.persistentDataContainer.set(reforgeNamespace, PersistentDataType.BYTE, 1)
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.displayName(
			Component.translatable(Material.LAPIS_LAZULI.translationKey())
				.append(Component.text(" chargé"))
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)
	}
	return itemStack
}
