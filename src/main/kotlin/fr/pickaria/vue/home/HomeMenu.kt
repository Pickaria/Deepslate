package fr.pickaria.vue.home

import fr.pickaria.DEFAULT_MENU
import fr.pickaria.controller.economy.balance
import fr.pickaria.controller.home.homeEntries
import fr.pickaria.controller.libraries.datetime.autoFormat
import fr.pickaria.controller.libraries.luckperms.displayName
import fr.pickaria.controller.libraries.luckperms.group
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.economy.toController
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.SkullMeta
import kotlin.time.Duration.Companion.seconds

@OptIn(ItemBuilderUnsafe::class)
internal fun homeMenu() = menu(DEFAULT_MENU) {
	rows = 6
	title = Component.text("Accueil", NamedTextColor.GOLD, TextDecoration.BOLD)

	fill(Material.WHITE_STAINED_GLASS_PANE, true)

	// All home entries
	homeEntries.forEachIndexed { index, entry ->
		item {
			position = index + 1 to 3
			material = entry.itemStack.type
			title = entry.itemStack.itemMeta.displayName()
			lore = entry.itemStack.itemMeta.lore() ?: emptyList()
			leftClick = Result.NONE to "/menu ${entry.entry.key}"
			setMeta(entry.itemStack.itemMeta)
			editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
			}
		}
	}

	val totalWorldTime = (opener.getStatistic(Statistic.TOTAL_WORLD_TIME) / 20).seconds

	// Player head
	item {
		position = 2 to 1
		material = Material.PLAYER_HEAD
		title = opener.displayName()
		lore {
			keyValues {
				"Grade" to (opener.group?.displayName() ?: Component.empty())
				"Temps de jeu" to totalWorldTime.autoFormat()
				"Crédits" to Credit.toController().format(opener.balance(Credit))
				"Clés" to Key.toController().format(opener.balance(Key))
				"Éclats de Pickarite" to Shard.toController().format(opener.balance(Shard))
			}
		}
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