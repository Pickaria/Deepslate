package fr.pickaria.reforge

import fr.pickaria.shared.GlowEnchantment
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

/*
Common (Colourless)
Uncommon (Green)
Rare (Blue)
Epic (Purple)
Legendary (Gold/Orange)
Relic (Red)
Resplendent (Rainbow)
Shadow (Black)
Radiant (White)
Stellar (Yellow)
Crystal (Aqua)
 */
fun getAttributeTitle(amount: Int) = if (amount >= 13) {
	Component.text("Crystal", NamedTextColor.AQUA)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 11) {
	Component.text("Stellaire", NamedTextColor.YELLOW)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 9) {
	Component.text("Radiant", NamedTextColor.WHITE)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 8) {
	Component.text("Obscur", NamedTextColor.BLACK)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 7) {
	Component.text("Resplendissant", NamedTextColor.YELLOW) // Rainbow
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 6) {
	Component.text("Relique", NamedTextColor.RED)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 5) {
	Component.text("Légendaire", NamedTextColor.GOLD)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 4) {
	Component.text("Épique", NamedTextColor.DARK_PURPLE)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 3) {
	Component.text("Rare", NamedTextColor.BLUE)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else if (amount >= 2) {
	Component.text("Peu commun", NamedTextColor.GREEN)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
} else {
	Component.text("Commun", NamedTextColor.GRAY)
		.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
}

fun getAttributeItem(): ItemStack {
	val itemStack = ItemStack(Material.LAPIS_LAZULI)
	itemStack.editMeta {
		it.persistentDataContainer.set(namespace, PersistentDataType.BYTE, 1)
		it.addEnchant(GlowEnchantment.instance, 1, true)
		it.displayName(
			Component.translatable(Material.LAPIS_LAZULI.translationKey())
				.append(Component.text(" chargé"))
				.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		)
	}
	return itemStack
}

fun ItemStack.isAttributeItem() = type == Material.LAPIS_LAZULI && itemMeta.persistentDataContainer.has(namespace)