package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.createInventory
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

data class Menu(
	private val title: Component,
	private val size: Int,
	private val items: Map<Int, Item.Builder>,
	val opener: Player,
	val previous: Menu? = null
) {
	companion object {
		operator fun invoke(opener: Player, previous: Menu?, init: BuilderInit<Builder>) =
			Builder(opener, previous).apply { init() }
	}

	private lateinit var holder: InventoryHolder

	private val inventory: Inventory
		get() = holder.inventory

	/**
	 * Creates a new inventory and places items in it.
	 */
	fun inventory(): Inventory {
		holder = Holder(this).apply {
			inventory = createInventory(this, size, title)
		}

		return holder.inventory
	}

	fun refresh() {
		// Place items in menu
		for ((slot, item) in items) {
			inventory.setItem(slot, item(this).itemStack)
		}
	}

	/**
	 * Handles the InventoryClickEvent for the current menu.
	 */
	operator fun invoke(event: InventoryClickEvent) = items[event.rawSlot]?.invoke(this)?.callback(event)

	class Builder(val opener: Player, val previous: Menu? = null) {
		private val items = mutableMapOf<Int, Item.Builder>()

		fun item(init: Item.Builder.() -> Unit): Item.Builder = Item(init).also {
			items[it.slot] = it
		}

		var title: Component = Component.empty()
		var rows: Int = 6
			set(value) {
				if (rows in 1..6) {
					field = value
				} else {
					throw RuntimeException("Invalid row number provided")
				}
			}

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		fun build(): Menu {
			return Menu(title, rows * 9, items, opener, previous)
		}
	}
}