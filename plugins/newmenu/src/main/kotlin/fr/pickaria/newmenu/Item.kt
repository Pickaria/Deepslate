package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class Item(
	val slot: Int,
	val itemStack: ItemStack,
	val leftClick: ClickHandler? = null,
	val rightClick: ClickHandler? = null
) {
	companion object {
		fun build(init: Builder.() -> Unit): Item = Builder().apply(init).build()
	}

	class Builder {
		private var leftClick: ClickHandler? = null
		private var rightClick: ClickHandler? = null

		fun leftClick(fn: ClickHandler) {
			leftClick = fn
		}

		fun rightClick(fn: ClickHandler) {
			rightClick = fn
		}

		var position: Pair<Int, Int> = Pair(0, 0)
		var material: Material = Material.AIR
		var title: Component? = null
		var lore: List<Component> = listOf()

		fun lore(init: Lore.() -> Unit) {
			lore = Lore().apply(init).build()
		}

		fun build(): Item {
			val itemStack = ItemStack(material)

			itemStack.editMeta {
				it.displayName(title)
				it.lore(lore)
			}

			val slot = position.second * 9 + position.first

			return Item(slot, itemStack, leftClick, rightClick)
		}
	}
}