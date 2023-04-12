package fr.pickaria.controller.reforge

import fr.pickaria.model.reforge.reforgeConfig
import fr.pickaria.model.reforge.reforgeNamespace
import fr.pickaria.shared.GlowEnchantment
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

fun getAttributeItem(amount: Int = 1): ItemStack {
	val itemStack = ItemStack(Material.LAPIS_LAZULI, amount)
	itemStack.editMeta {
		it.persistentDataContainer.set(reforgeNamespace, PersistentDataType.BYTE, 1)
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.displayName(
			Component.translatable(Material.LAPIS_LAZULI.translationKey())
				.appendSpace()
				.append(Component.text(reforgeConfig.chargedLapisName))
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				.color(NamedTextColor.GOLD)
		)

		val description = reforgeConfig.chargedLapisDescription.map { line ->
			MiniMessage(line).message.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		}

		it.lore(description)
	}
	return itemStack
}
