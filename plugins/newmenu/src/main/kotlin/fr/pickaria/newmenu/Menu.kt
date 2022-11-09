package fr.pickaria.newmenu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.createInventory
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

data class Menu(private val title: Component, private val size: Int, private val items: Map<Int, Item>) {
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

		// Place items in menu
		for ((slot, item) in items) {
			inventory.setItem(slot, item.itemStack)
		}

		return holder.inventory
	}

	/**
	 * Handles the InventoryClickEvent for the current menu.
	 */
	operator fun invoke(event: InventoryClickEvent) {
		items[event.rawSlot]?.let {
			if (event.isLeftClick) {
				it.leftClick?.invoke(event)
			} else if (event.isRightClick) {
				it.rightClick?.invoke(event)
			} else {
				null
			}
		}
	}

	class Builder {
		var title: Component = Component.empty()
		var rows: Int = 6
		var items: MutableMap<Int, Item> = mutableMapOf()

		/**
		 * Instantiate a new item in the menu.
		 */
		fun item(init: Item.Builder.() -> Unit): Item = Item(init).also {
			items[it.slot] = it
		}

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		operator fun invoke(): Menu = Menu(title, rows * 9, items)
	}
}
