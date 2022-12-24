package fr.pickaria.vue.home

import fr.pickaria.DEFAULT_MENU
import fr.pickaria.controller.economy.balance
import fr.pickaria.controller.home.homeEntries
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import org.bukkit.inventory.meta.SkullMeta

internal fun homeMenu() = menu(DEFAULT_MENU) {
	rows = 6
	title = Component.text("Accueil", NamedTextColor.GOLD, TextDecoration.BOLD)

	fill(Material.WHITE_STAINED_GLASS_PANE, true)

	homeEntries.forEachIndexed { index, entry ->
		item {
			position = index + 1 to 3
			material = entry.material
			title = entry.title
			lore = entry.lore
			leftClick = Result.NONE to "/menu ${entry.entry.key}"
			editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
			}
		}
	}

	item {
		position = 2 to 1
		material = Material.PLAYER_HEAD
		title = opener.displayName()
		editMeta {
			(it as SkullMeta).owningPlayer = opener
		}
	}

	item {
		position = 6 to 1
		material = Material.BUNDLE
		title = Component.text("Sacoche de Pickarite", NamedTextColor.GOLD)
		editMeta {
			var balance = opener.balance(Shard).toInt()
			val items = mutableListOf<ItemStack>()

			for (i in 0 until balance step 64) {
				val amount = if (balance > 64) 64 else balance
				items.add(Shard.toController().item(amount))
				balance -= 64
			}

			it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
			(it as BundleMeta).setItems(items)
		}
		lore {
			description {
				-"Dépensez vos éclats de Pickarite"
				-"au lobby du serveur !"
			}
		}
	}

	closeItem()
}