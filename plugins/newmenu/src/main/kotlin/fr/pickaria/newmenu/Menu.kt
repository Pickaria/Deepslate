package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.createInventory
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

data class Menu(private val title: Component, private val size: Int, private val items: Map<Int, Item.Builder>) {
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
			inventory.setItem(slot, item.itemStack)
		}
	}

	/**
	 * Handles the InventoryClickEvent for the current menu.
	 */
	operator fun invoke(event: InventoryClickEvent) = items[event.rawSlot]?.callback(event)

	class Builder {
		var title: Component = Component.empty()
		var rows: Int = 6
		private var items: MutableMap<Int, Item.Builder> = mutableMapOf()
		var previous: Menu? = null

		/**
		 * Instantiate a new item in the menu.
		 */
		fun item(init: Item.Builder.() -> Unit): Item.Builder = Item(init).also {
			items[it.slot] = it
		}

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		operator fun invoke(): Menu = Menu(title, rows * 9, items)

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		operator fun invoke(previous: Menu? = null): Menu {
			this.previous = previous
			return Menu(title, rows * 9, items)
		}
	}
}
