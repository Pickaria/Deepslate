package fr.pickaria.vue.town

import com.palmergames.bukkit.towny.TownyAPI
import fr.pickaria.controller.home.addToHome
import fr.pickaria.menu.*
import fr.pickaria.model.town.flag
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.ceil

@OptIn(ItemBuilderUnsafe::class)
fun townMenu() = menu("towns") {
	title = Component.text("Villes")

	val towny = TownyAPI.getInstance()
	val count = towny.towns.size

	rows = (ceil(count / 9.0).toInt())
		.inc()
		.coerceAtLeast(2)
		.coerceAtMost(6)

	val pageSize = (size - 9).coerceAtLeast(9)
	val start = page * pageSize
	val towns = towny.towns.slice(start until (start + pageSize).coerceAtMost(count))
	val maxPage = (count - 1) / pageSize

	towns.forEachIndexed { index, town ->
		val flag = town.flag.apply {
			addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
		}

		val registered = Instant.fromEpochMilliseconds(town.registered)
			.toLocalDateTime(TimeZone.currentSystemDefault())
			.toJavaLocalDateTime()

		val formatted = DateTimeFormatter
			.ofLocalizedDate(FormatStyle.FULL)
			.localizedBy(Locale.FRENCH)
			.format(registered)

		item {
			flag.let {
				material = it.type
				setMeta(it.itemMeta)
			}

			title = Component.text(town.name)
			slot = index

			lore {
				keyValues {
//					"Solde" to Credit.economy.format(town.account.holdingBalance)
					"Résidents" to town.numResidents
					"Date de création" to formatted
				}

				leftClick = "Clic-gauche pour visiter la ville"

				if (town.isOpen) {
					rightClick = "Clic-droit pour rejoindre la ville"
				}
			}
		}
	}

	previousPage()
	closeItem()
	nextPage(maxPage)
}.addToHome(Material.WHITE_BANNER, Component.text("Villes")) {
	description {
		-"Répertoire des villes du serveur."
	}
}