package fr.pickaria.vue.market

import fr.pickaria.controller.menu.addToHome
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.market.Order
import fr.pickaria.model.market.marketConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import kotlin.math.max

@OptIn(ItemBuilderUnsafe::class)
internal fun orderListingMenu() = menu("market") {
    title = Component.text("Marché", NamedTextColor.GOLD, TextDecoration.BOLD)

    val count = Order.count()
    val pageSize = size - 9
    val start = page * pageSize
    val orders = Order.getListings(pageSize, start.toLong())
    val maxPage = (count - 1) / pageSize

    orders.forEachIndexed { index, order ->
        val sellPrice = max(order.averagePrice * marketConfig.sellPercentage, marketConfig.minimumPrice)

        item {
            slot = index
            material = order.material
            title = Component.translatable(order.material.translationKey())
            lore {
                keyValues {
                    "Quantité en vente" to order.amount
                    "Achat à partir de" to Credit.economy.format(order.minimumPrice)
                    "Vente immédiate à" to Credit.economy.format(sellPrice)
                    "Prix moyen" to Credit.economy.format(order.averagePrice)
                }
                leftClick = "Clic-gauche pour voir les options d'achat"
                rightClick = "Clic-droit pour voir les options de vente"
            }
            leftClick {
                opener.open(buyMenu(order.material))
            }
            rightClick {
                opener.open(sellMenu(order.material))
            }
        }
    }

    previousPage()
    closeItem()
    nextPage(maxPage.toInt())
}.addToHome(Material.EMERALD, Component.text("Marché", NamedTextColor.GOLD)) {
    description {
        -"Achetez et vendez toute sorte de matériaux."
    }
}