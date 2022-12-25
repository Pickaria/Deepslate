package fr.pickaria.vue.town

import fr.pickaria.menu.*
import fr.pickaria.model.town.Town
import net.kyori.adventure.text.Component
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.ceil

@OptIn(ItemBuilderUnsafe::class)
fun townMenu() = menu("towns") {
	title = Component.text("Villes")

	val count = transaction { Town.count() }

	rows = (ceil(count / 9.0).toInt())
		.inc()
		.coerceAtLeast(1)
		.coerceAtMost(6)

	val pageSize = size - 9
	val start = page * pageSize
	val towns = transaction { Town.all().limit(pageSize, start.toLong()) }
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