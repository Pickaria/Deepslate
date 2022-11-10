package fr.pickaria.newmenu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

typealias ClickHandler = ((InventoryClickEvent) -> Unit)
typealias BuilderInit<T> = T.() -> Unit

internal const val DEFAULT_MENU = "home"
internal val builders: MutableMap<String, BuilderInit<Menu.Builder>> = mutableMapOf()

fun register(name: String, builder: BuilderInit<Menu.Builder>) {
	if (builders.containsKey(name)) {
		Bukkit.getServer().logger.warning("Registering menu `$name` but is already registered.")
	}

	builders[name] = builder
}

fun unregister(name: String) {
	if (!builders.containsKey(name)) {
		Bukkit.getServer().logger.warning("Trying to unregister menu `$name` but is not registered.")
	} else {
		builders.remove(name)
	}
}

fun menu(name: String, init: BuilderInit<Menu.Builder>) = register(name, init)

/**
 * Opens an already instantiated menu.
 */
infix fun Player.open(menu: Menu) {
	menu.inventory().let {
		menu.refresh()
		openInventory(it)
	}
}

/**
 * Builds and opens a menu at a given page.
 */
infix fun Player.open(menu: String): Boolean =
	builders[menu]?.let {
		val previous = (openInventory.topInventory.holder as? Holder)?.menu
		this open Menu(it, menu, this@open, previous, 0).build()
		true
	} ?: false

/**
 * Builds and opens a menu at a given page, the previous for the new menu is the previous menu's previous.
 */
fun Player.open(menu: String, page: Int): Boolean =
	builders[menu]?.let {
		val previous = (openInventory.topInventory.holder as? Holder)?.menu?.previous
		this open Menu(it, menu, this@open, previous, page).build()
		true
	} ?: false
