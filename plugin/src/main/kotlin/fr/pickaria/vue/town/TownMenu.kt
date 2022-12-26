package fr.pickaria.vue.town

import fr.pickaria.controller.town.TownController
import fr.pickaria.controller.town.hasTownPermission
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.town.TownPermission
import kotlinx.datetime.toJavaLocalDateTime
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemFlag
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.ceil

@OptIn(ItemBuilderUnsafe::class)
fun townMenu() = menu("towns") {
	title = Component.text("Villes")

	val count = TownController.count()

	rows = (ceil(count / 9.0).toInt())
		.inc()
		.coerceAtLeast(2)
		.coerceAtMost(6)

	val pageSize = (size - 9).coerceAtLeast(9)
	val start = page * pageSize
	val towns = TownController.all(pageSize, start.toLong())
	val maxPage = (count - 1) / pageSize

	towns.forEachIndexed { index, town ->
		val flag = town.flag.apply {
			addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS)
		}

		val formatted = DateTimeFormatter
			.ofLocalizedDate(FormatStyle.FULL)
			.localizedBy(Locale.FRENCH)
			.format(town.creationDate.toJavaLocalDateTime())

		item {
			material = flag.type
			title = flag.itemMeta.displayName()
			slot = index
			setMeta(flag.itemMeta)
			lore {
				keyValues {
					"Solde" to Credit.economy.format(town.balance)
					"Résidents" to town.residentCount
					"Date de création" to formatted
				}

				leftClick = "Clic-gauche pour visiter la ville"

				if (opener.hasTownPermission(town, TownPermission.JOIN)) {
					rightClick = "Clic-droit pour rejoindre la ville"
				}
			}
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage.toInt())
}