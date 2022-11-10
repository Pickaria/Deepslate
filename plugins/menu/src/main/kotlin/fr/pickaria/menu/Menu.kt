package fr.pickaria.menu

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
	private val builder: BuilderInit<Builder>,
	val opener: Player,
	internal val previous: Menu? = null
) {
	companion object {
		internal operator fun invoke(
			init: BuilderInit<Builder>,
			opener: Player,
			previous: Menu? = null,
			page: Int = 0,
			key: String? = null
		) =
			Builder(opener, init, previous, page, key).apply { init() }
	}

	private lateinit var holder: InventoryHolder

	private val inventory: Inventory
		get() = holder.inventory

	/**
	 * Creates a new inventory and places items in it.
	 */
	internal fun inventory(): Inventory {
		holder = Holder(this).apply {
			inventory = createInventory(this, size, title)
		}

		// Place items in menu
		for ((slot, item) in items) {
			inventory.setItem(slot, item(this).itemStack)
		}

		return holder.inventory
	}

	internal fun rebuild() {
		opener.open(builder, previous)
		inventory.close()
	}

	/**
	 * Handles the InventoryClickEvent for the current menu.
	 */
	internal operator fun invoke(event: InventoryClickEvent) = items[event.rawSlot]?.invoke(this)?.callback(event)

	class Builder(
		val opener: Player,
		private val init: BuilderInit<Builder>,
		val previous: Menu? = null,
		val page: Int = 0,
		val key: String? = null
	) {
		private val items = mutableMapOf<Int, Item.Builder>()

		fun item(init: Item.Builder.() -> Unit): Item.Builder = Item(init).also {
			if (it.slot < size) {
				items[it.slot] = it
			} else {
				throw RuntimeException("Invalid item position, inventory of size $size, item in index ${it.slot}.")
			}
		}

		internal infix fun has(index: Int) = items.contains(index)

		var title: Component = Component.empty()

		var rows: Int = 6
			set(value) {
				if (rows in 1..6) {
					field = value
				} else {
					throw RuntimeException("Invalid row number provided, must be between 1 and 6.")
				}
			}

		val size: Int
			get() = rows * 9

		/**
		 * Creates and returns a new instance of the menu with given parameters.
		 */
		internal fun build(): Menu {
			return Menu(title, size, items, init, opener, previous)
		}
	}
}