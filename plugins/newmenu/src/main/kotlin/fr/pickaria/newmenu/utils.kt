package fr.pickaria.newmenu

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

typealias ClickHandler = ((InventoryClickEvent) -> Unit)
internal val builders: MutableMap<String, Menu.Builder> = mutableMapOf()

fun register(name: String, builder: Menu.Builder) {
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

fun menu(init: Menu.Builder.() -> Unit): Menu.Builder = Menu.Builder().apply(init)

infix fun Player.open(menu: Menu) {
	openInventory(menu.inventory())
}

infix fun Player.open(menu: String): Boolean =
	builders[menu]?.build()?.let {
		this open it
		true
	} ?: false

internal const val DEFAULT_MENU = "home"