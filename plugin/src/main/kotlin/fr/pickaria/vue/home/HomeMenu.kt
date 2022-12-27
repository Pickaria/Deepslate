package fr.pickaria.vue.home

import fr.pickaria.DEFAULT_MENU
import fr.pickaria.controller.economy.balance
import fr.pickaria.controller.home.homeEntries
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta

@OptIn(ItemBuilderUnsafe::class)
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

	val bundle = Shard.toController().bundle(opener.balance(Shard))

	item {
		position = 6 to 1
		material = bundle.type
		title = bundle.itemMeta.displayName()
		setMeta(bundle.itemMeta)

		lore {
			description {
				-"Dépensez vos éclats de Pickarite"
				-"au lobby du serveur !"
			}
		}
	}

	closeItem()
}