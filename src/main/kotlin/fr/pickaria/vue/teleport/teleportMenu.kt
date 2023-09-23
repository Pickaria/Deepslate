package fr.pickaria.vue.teleport

import fr.pickaria.controller.teleport.getHomes
import fr.pickaria.menu.Result
import fr.pickaria.menu.closeItem
import fr.pickaria.menu.fill
import fr.pickaria.menu.menu
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material


fun teleportMenu() = menu("teleport") {
    rows = 6
    title = Component.text("Téléportations", NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)

    fill(Material.WHITE_STAINED_GLASS_PANE, true)

    item {
        position = 2 to 1
        material = Material.CHISELED_STONE_BRICKS
        title = Component.text("Lobby")

        lore {
            leftClick = "Clic-gauche pour aller au lobby"
        }

        leftClick = Result.CLOSE to "/lobby"
    }

    item {
        position = 6 to 1
        material = Material.COMPASS
        title = Component.text("Spawn")

        lore {
            leftClick = "Clic-gauche pour aller au spawn"
        }

        leftClick = Result.CLOSE to "/spawn"
    }

    opener.getHomes().forEachIndexed { index, home ->
        item {
            position = index + 1 to 3
            material = home.material
            title = home.title

            lore {
                leftClick = "Clic-gauche pour se téléporter"
            }

            leftClick = Result.CLOSE to "/home teleport ${home.name}"
        }
    }

    closeItem()
}