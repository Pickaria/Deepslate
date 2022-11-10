package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player


fun Menu.Builder.closeItem() {
	item {
		position = 4 to rows - 1

		previous?.let {
			// Go back
			title = Component.text("Retour")
			material = Material.ARROW
			leftClick {
				(it.whoClicked as Player) open previous
			}
			lore {
				leftClick = "Clic-gauche pour retourner au précédent menu"
			}
		} ?: run {
			// Close menu
			title = Component.text("Fermer")
			material = Material.BARRIER
			leftClick {
				it.whoClicked.closeInventory()
			}
			lore {
				leftClick = "Clic-gauche pour fermer le menu"
			}
		}
	}
}

fun Menu.Builder.previousPage() {
	if (page > 0) {
		item {
			material = Material.PAPER
			title = Component.text("Page précédente")
			position = 2 to rows - 1
			leftClick = "/newmenu $key ${page - 1}"
		}
	}
}

fun Menu.Builder.nextPage(maxPage: Int) {
	if (page < maxPage) {
		item {
			material = Material.PAPER
			title = Component.text("Page suivante")
			position = 6 to rows - 1
			leftClick = "/newmenu $key ${page + 1}"
		}
	}
}

fun Menu.Builder.fill(material: Material, ignoreLastRow: Boolean = false) {
	val end = (if (ignoreLastRow) size - 9 else size) - 1

	for (i in 0..end) {
		if (this has i) continue

		item {
			title = Component.empty()
			this.material = material
			slot = i
		}
	}
}