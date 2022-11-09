package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

internal fun homeMenu() = menu(DEFAULT_MENU) {
	rows = 3
	title = Component.text("menu")

	item {
		material = Material.GRASS_BLOCK
		leftClick = "/me This works!"
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
		position = Pair(4, 1)
		material = Material.YELLOW_CONCRETE
		lore {
			description {
				-"ligne 1"
				-"ligne 2"
			}
			keyValues {
				"Yellow" to "Best color"
			}
		}
	}
}