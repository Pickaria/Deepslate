package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

data class Item(
	val slot: Int,
	val itemStack: ItemStack,
	val leftClick: ClickHandler? = null,
	val rightClick: ClickHandler? = null
) {
	companion object {
		operator fun invoke(init: Builder.() -> Unit): Builder = Builder().apply(init)
	}

	class Builder {
		private var leftClickCallback: ClickHandler? = null
		private var rightClickCallback: ClickHandler? = null

		var leftClick: String? = null
			set(value) = leftClick { event ->
				value?.let {
					(event.whoClicked as Player).chat(value)
				}
			}

		var rightClick: String? = null
			set(value) = rightClick { event ->
				value?.let {
					(event.whoClicked as Player).chat(value)
				}
			}

		@Deprecated("Menu should help player run a command.", replaceWith = ReplaceWith("leftClick = \"/command\""))
		fun leftClick(fn: ClickHandler) {
			leftClickCallback = fn
		}

		@Deprecated("Menu should help player run a command.", replaceWith = ReplaceWith("rightClick = \"/command\""))
		fun rightClick(fn: ClickHandler) {
			rightClickCallback = fn
		}

		fun callback(event: InventoryClickEvent) = if (event.isLeftClick) {
			leftClickCallback?.invoke(event)
		} else if (event.isRightClick) {
			rightClickCallback?.invoke(event)
		} else {
			null
		}

		var position: Pair<Int, Int> = Pair(0, 0)
		var material: Material = Material.AIR
		var title: Component? = null
		private var lore: List<Component> = listOf()

		val slot: Int
			get() = position.second * 9 + position.first

		val itemStack: ItemStack
			get() {
				val itemStack = ItemStack(material)

				itemStack.editMeta {
					it.displayName(title)
					it.lore(lore)
				}

				return itemStack
			}

		fun lore(init: Lore.() -> Unit) {
			lore = Lore(init)
		}

		operator fun invoke(): Item = Item(slot, itemStack, leftClickCallback, rightClickCallback)
	}
}