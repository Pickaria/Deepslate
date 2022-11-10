package fr.pickaria.menu.home

import fr.pickaria.menu.*
import fr.pickaria.shard.createShardItem
import fr.pickaria.shard.getShardBalance
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BundleMeta
import org.bukkit.inventory.meta.SkullMeta
import kotlin.time.ExperimentalTime

internal fun foodMenu() = menu("food") {
	rows = 4
	title = Component.text("Nourritures", NamedTextColor.GREEN)

	val pageSize = size - 10
	val start = page * pageSize
	val materials = Material.values().filter { it.isEdible }

	for (i in 0..pageSize) {
		materials.getOrNull(start + i)?.let {
			item {
				material = it
				title = Component.translatable(it.translationKey())
				slot = i
			}
		}
	}

	previousPage()
	closeItem()
	nextPage(materials.size / pageSize)
}.addToHome(Material.COOKED_PORKCHOP, Component.text("Nourritures", NamedTextColor.GREEN)) {
	description {
		-"Menu contenant tous les aliments."
	}
}

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
			leftClick = "/menu ${entry.entry.key}"
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
			var balance = getShardBalance(opener as OfflinePlayer)
			val items = mutableListOf<ItemStack>()

			for (i in 0 until balance step 64) {
				val amount = if (balance > 64) 64 else balance
				items.add(createShardItem(amount))
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