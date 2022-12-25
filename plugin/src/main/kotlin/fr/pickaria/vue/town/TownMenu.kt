package fr.pickaria.vue.town

import fr.pickaria.controller.town.TownController
import fr.pickaria.menu.*
import net.kyori.adventure.text.Component
import kotlin.math.ceil

@OptIn(ItemBuilderUnsafe::class)
fun townMenu() = menu("towns") {
	title = Component.text("Villes")

	val count = TownController.count()

	rows = (ceil(count / 9.0).toInt())
		.inc()
		.coerceAtLeast(1)
		.coerceAtMost(6)

	val pageSize = size - 9
	val start = page * pageSize
	val towns = TownController.all(pageSize, start.toLong())
	val maxPage = (count - 1) / pageSize

	towns.forEachIndexed { index, town ->
		val flag = town.flag

		item {
			material = flag.type
			title = flag.itemMeta.displayName()
			slot = index
			setMeta(flag.itemMeta)
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage.toInt())
}