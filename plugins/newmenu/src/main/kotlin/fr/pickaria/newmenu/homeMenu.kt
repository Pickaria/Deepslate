package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

internal fun homeMenu() = menu {
	rows = 3
	title = Component.text("menu")

	item {
		material = Material.GRASS_BLOCK
		leftClick {
			it.whoClicked.sendMessage("This works!")
		}
		rightClick {
			it.inventory.addItem(ItemStack(Material.GRASS))
		}
		lore {
			description = listOf("Send a small message to the player.")
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
			keyValues = mapOf("Yellow" to "Best color")
		}
	}
}