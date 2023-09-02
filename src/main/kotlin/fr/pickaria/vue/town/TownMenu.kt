package fr.pickaria.vue.town

import fr.pickaria.controller.menu.addToHome
import fr.pickaria.menu.*
import fr.pickaria.model.town.Town
import fr.pickaria.model.town.flag
import kotlinx.datetime.toJavaLocalDateTime
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.math.ceil

@OptIn(ItemBuilderUnsafe::class)
fun townMenu() = menu("towns") {
    // FIXME: Remove global transaction
    transaction {
        title = Component.text("Villes")

        val count = Town.count()

        rows = (ceil(count / 9.0).toInt())
            .inc()
            .coerceAtLeast(2)
            .coerceAtMost(6)

        val pageSize = (size - 9).coerceAtLeast(9)
        val start = page * pageSize
        val towns = Town.all().limit(pageSize, start.toLong())
        val maxPage = (count - 1) / pageSize

        towns.forEachIndexed { index, town ->
            val flag = town.flag.apply {
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ITEM_SPECIFICS)
            }

            val registered = town.createdAt.toJavaLocalDateTime()

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
                        "Résidents" to town.members.count()
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
        nextPage(maxPage.toInt())
    }
}.addToHome(Material.WHITE_BANNER, Component.text("Villes")) {
    description {
        -"Répertoire des villes du serveur."
    }
}