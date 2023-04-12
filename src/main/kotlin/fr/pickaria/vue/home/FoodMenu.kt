package fr.pickaria.vue.home

import fr.pickaria.controller.home.addToHome
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.menu
import fr.pickaria.menu.nextPage
import fr.pickaria.menu.previousPage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

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