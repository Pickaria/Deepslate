package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

internal fun foodMenu() = menu("food") {
	rows = 4
	title = Component.text("Nourritures", NamedTextColor.GREEN)

	val pageSize = rows * 9 - 10
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
}

internal fun homeMenu() = menu(DEFAULT_MENU) {
	rows = 4
	title = Component.text("Home menu")

	fill(Material.GREEN_STAINED_GLASS_PANE, true)

	item {
		position = 7 to 1
		material = Material.PLAYER_HEAD
		title = opener.displayName()
	}

	item {
		position = 1 to 1
		material = Material.GRASS_BLOCK
		leftClick {
			it.whoClicked.sendMessage("This works!")
		}
		rightClick = "/give @s minecraft:grass"
		lore {
			description {
				-"Send a small message to the player."
			}
			leftClick = "Left click to receive a message!"
			rightClick = "Right click to receive grass!"
		}
	}

	item {
		title = Component.text("This is a yellow concrete", NamedTextColor.YELLOW)
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		position = 4 to 1
		material = Material.YELLOW_CONCRETE
		lore {
			description {
				-"ligne 1"
				-"ligne 2"
			}
			keyValues {
				"Yellow" to "Best color"
			}
			leftClick = "Left click to open a sub-menu"
		}
		leftClick = "/newmenu food"
	}

	closeItem()
}