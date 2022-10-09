package fr.pickaria.menu

import org.bukkit.entity.HumanEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.Inventory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.full.primaryConstructor

class MenuController(private val plugin: Main) : Listener {
	private val menus = ConcurrentHashMap<Inventory, BaseMenu>()

	fun openMenu(player: HumanEntity, menu: MenuEnum, previousMenu: BaseMenu?): Boolean =
		menu.menu.primaryConstructor?.let {
			val menuInstance = it.call(menu.title, player, previousMenu, 54) as BaseMenu
			openMenu(player, menuInstance)
			true
		} ?: run {
			player.sendMessage("§cUne erreur est survenue lors de l'ouverture du menu.")
			false
		}

	fun openMenu(player: HumanEntity, menu: BaseMenu) {
		menus.putIfAbsent(menu.inventory, menu)
		player.openInventory(menu.inventory)
	}

	@EventHandler
	private fun onInventoryClick(event: InventoryClickEvent) {
		menus[event.inventory]?.let {
			event.isCancelled = true
			it.onInventoryClick(event)
		}
	}

	@EventHandler
	private fun onInventoryDrag(e: InventoryDragEvent) {
		if (menus.containsKey(e.inventory)) {
			e.isCancelled = true
		}
	}

	@EventHandler
	private fun onInventoryOpen(event: InventoryOpenEvent) {
		menus[event.inventory]?.updateMenu()
	}

	@EventHandler
	private fun onInventoryClose(event: InventoryCloseEvent) {
		menus.remove(event.inventory)
	}
}