package fr.pickaria.vue.miniblocks

import fr.pickaria.controller.menu.addToHome
import fr.pickaria.controller.miniblocks.toController
import fr.pickaria.menu.*
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.miniblocks.miniBlocksConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

private val miniBlockMenuItem = miniBlocksConfig.miniBlocks.random().toController().create().apply {
    editMeta {
        it.displayName(Component.text("Mini blocs"))
        val lore = Lore {
            description {
                -"Répertoire des mini blocs."
            }
        }
        it.lore(lore.build())
    }
}

@OptIn(ItemBuilderUnsafe::class)
fun miniBlocksMenu() = menu("miniblocks") {
    title = Component.text("Mini blocs", NamedTextColor.DARK_BLUE, TextDecoration.BOLD)

    val count = miniBlocksConfig.sortedMiniBlocks.size
    val pageSize = size - 9
    val start = page * pageSize
    val maxPage = (count - 1) / pageSize
    val miniBlocks = miniBlocksConfig.sortedMiniBlocks.slice(start until (start + pageSize).coerceAtMost(count))

    miniBlocks.forEachIndexed { index, miniBlock ->
        item {
            slot = index
            material = Material.PLAYER_HEAD
            title = Component.translatable(miniBlock.material.translationKey())

            setMeta(miniBlock.toController().create().itemMeta)

            lore {
                keyValues {
                    "Bloc" to Component.translatable(miniBlock.material.translationKey()).asComponent()
                    "Prix unitaire" to Credit.economy.format(miniBlock.price ?: miniBlocksConfig.defaultPrice)
                }
                leftClick = "Clic-gauche pour acheter"
            }

            leftClick {
                opener open miniBlockBuyMenu(miniBlock)
            }
        }
    }

    previousPage()
    closeItem()
    nextPage(maxPage)
}.addToHome(miniBlockMenuItem)