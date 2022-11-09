package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material

internal fun testMenu() = menu("test") {
	rows = 3
	title = Component.text("test")
}

internal fun homeMenu() = menu(DEFAULT_MENU) {
	rows = 3
	title = Component.text("menu")

	item { (opener, _) ->
		position = Pair(8, 0) // Last column on first row
		material = Material.PLAYER_HEAD
		title = opener.displayName()

	}

	item {
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
			leftClick = "Left click to open a sub-menu"
		}
		leftClick = "/newmenu test"
	}

	//closeItem()
}

/*fun Menu.Builder.closeItem() {
	item {
		position = Pair(4, rows - 1)

		// Go back
		title = Component.text("Retour")
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		material = Material.ARROW
		leftClick {
			(it.whoClicked as Player) open this@closeItem.previous!!
		}
		lore {
			leftClick = "Clic-gauche pour retourner au précédent menu"
		}

		// Close menu
		title = Component.text("Fermer")
			.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
		material = Material.BARRIER
		leftClick {
			it.whoClicked.closeInventory()
		}
		lore {
			leftClick = "Clic-gauche pour fermer le menu"
		}
	}
}*/