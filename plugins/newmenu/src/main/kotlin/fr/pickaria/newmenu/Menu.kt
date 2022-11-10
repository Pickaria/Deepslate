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

	class Builder {
		private var itemBinders = mutableListOf<Binder<ItemBuilderConfig>>()
		fun item(init: ItemBuilderConfig) {
			itemBinders.add { _, _ -> init }
		}

		var title: Component = Component.empty()
		var rows: Int = 6

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		operator fun invoke(opener: Player, previous: Menu? = null): Menu {
			val items = itemBinders.associate {
				val item = Item(opener, previous, it(opener, previous))
				item.slot to item
			}
			return Menu(title, rows * 9, items, opener, previous)
		}
	}
}

typealias Binder<T> = (opener: Player, previous: Menu?) -> T

typealias OpenMenuData = (Pair<Player, Menu?>)

typealias ItemBuilderConfig = Item.Builder.(data: OpenMenuData) -> Unit