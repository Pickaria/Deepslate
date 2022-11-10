package fr.pickaria.menu

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

typealias ClickCallback = Pair<Result, String?>
private typealias FullClickCallback = Triple<Result, String?, ClickHandler?>

data class Item(
	val menu: Menu,
	val slot: Int,
	val itemStack: ItemStack,
	val leftClick: FullClickCallback,
	val rightClick: FullClickCallback
) {
	companion object {
		operator fun invoke(init: BuilderInit<Builder>): Builder =
			Builder().apply { init() }
	}

	fun callback(event: InventoryClickEvent) {
		if (event.isLeftClick) {
			callback(event, leftClick)
		} else if (event.isRightClick) {
			callback(event, rightClick)
		}
	}

	private fun callback(event: InventoryClickEvent, click: FullClickCallback) {
		val player = event.whoClicked as Player

		println(click)

		click.second?.let {
			player.chat(it)
		}

		click.third?.invoke(event)

		when (click.first) {
			Result.CLOSE -> event.inventory.close()
			Result.PREVIOUS -> menu.previous?.let { player open it }
			Result.REFRESH -> menu.refresh()
			Result.NONE -> {}
		}
	}

	class Builder {
		private var leftClickCallback: FullClickCallback = Triple(Result.NONE, null, null)
		private var rightClickCallback: FullClickCallback = Triple(Result.NONE, null, null)

		var leftClick: ClickCallback = Result.NONE to null
			set(value) {
				field = value
				leftClickCallback = Triple(value.first, value.second, leftClickCallback.third)
			}
		var rightClick: ClickCallback = Result.NONE to null
			set(value) {
				field = value
				rightClickCallback = Triple(value.first, value.second, rightClickCallback.third)
			}

		@Deprecated("Menu should help player run a command.", replaceWith = ReplaceWith("leftClick = \"/command\""))
		fun leftClick(fn: ClickHandler) {
			leftClickCallback = Triple(leftClick.first, leftClick.second, fn)
		}

		@Deprecated("Menu should help player run a command.", replaceWith = ReplaceWith("rightClick = \"/command\""))
		fun rightClick(fn: ClickHandler) {
			rightClickCallback = Triple(rightClick.first, rightClick.second, fn)
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