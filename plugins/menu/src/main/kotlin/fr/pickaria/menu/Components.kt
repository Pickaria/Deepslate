package fr.pickaria.menu

import net.kyori.adventure.text.Component
import org.bukkit.Material


fun Menu.Builder.closeItem() {
	item {
		position = 4 to rows - 1

		previous?.let {
			// Go back
			title = Component.text("Retour")
			material = Material.ARROW
			leftClick = Result.PREVIOUS to null
			lore {
				leftClick = "Clic-gauche pour retourner au précédent menu"
			}
		} ?: run {
			// Close menu
			title = Component.text("Fermer")
			material = Material.BARRIER
			leftClick = Result.CLOSE to null
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
			leftClick = Result.NONE to "/menu $key ${page - 1}"
		}
	}
}

fun Menu.Builder.nextPage(maxPage: Int) {
	if (page < maxPage) {
		item {
			material = Material.PAPER
			title = Component.text("Page suivante")
			position = 6 to rows - 1
			leftClick = Result.NONE to "/menu $key ${page + 1}"
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