package fr.pickaria.controller.town

import fr.pickaria.model.town.bookNamespace
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class TownHallRegister {
	companion object {
		fun create(): ItemStack {
			return ItemStack(Material.WRITTEN_BOOK).apply {
				editMeta { meta ->
					meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
					meta.displayName(
						Component.text("Registre vierge").decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
					)
					meta.lore(
						listOf(
							Component.text(
								"Registre utilisé pour la gestion d'une ville.",
								NamedTextColor.GRAY
							).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
						)
					)
					meta.persistentDataContainer.set(bookNamespace, PersistentDataType.BYTE, 1)
				}
			}
		}
	}
}
