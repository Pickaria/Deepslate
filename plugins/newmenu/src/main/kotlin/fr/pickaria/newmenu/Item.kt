package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

data class Item(
	val menu: Menu,
	val slot: Int,
	val itemStack: ItemStack,
	val leftClick: ClickHandler? = null,
	val rightClick: ClickHandler? = null
) {
	companion object {
		operator fun invoke(init: BuilderInit<Builder>): Builder =
			Builder().apply { init() }
	}

	fun callback(event: InventoryClickEvent) = if (event.isLeftClick) {
		leftClick?.invoke(event)
	} else if (event.isRightClick) {
		rightClick?.invoke(event)
	} else {
		null
	}

	class Builder {
		private var leftClickCallback: ClickHandler? = null
		private var rightClickCallback: ClickHandler? = null

		var leftClick: String? = null
			set(value) = leftClick { event ->
				value?.let {
					(event.whoClicked as Player).chat(value)
					event.inventory.close()
				}
			}

		var rightClick: String? = null
			set(value) = rightClick { event ->
				value?.let {
					(event.whoClicked as Player).chat(value)
					event.inventory.close()
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

		var position: Pair<Int, Int> = Pair(0, 0)
			set(value) {
				if (value.first in 0..8 && value.second in 0..5) {
					field = value
				} else {
					throw RuntimeException("Invalid position provided")
				}
			}

		var material: Material = Material.AIR
		var title: Component? = null
			set(value) {
				field = value?.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
			}
		var lore: List<Component> = listOf()

		fun lore(init: Lore.() -> Unit) {
			lore = Lore(init).build()
		}

		var slot: Int
			get() = position.second * 9 + position.first
			set(value) {
				position = Pair(value % 9, value / 9)
			}

		private var meta: ((ItemMeta) -> Unit)? = null

		fun editMeta(consumer: (ItemMeta) -> Unit) {
			meta = consumer
		}

		private val itemStack: ItemStack
			get() {
				val itemStack = ItemStack(material)

				itemStack.editMeta {
					it.displayName(title)
					it.lore(lore)
					meta?.invoke(it)
				}

				return itemStack
			}

		operator fun invoke(menu: Menu): Item = Item(menu, slot, itemStack, leftClickCallback, rightClickCallback)
	}
}