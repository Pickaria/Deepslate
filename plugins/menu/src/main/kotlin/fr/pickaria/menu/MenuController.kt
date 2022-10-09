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

class MenuController : Listener {
	private val registeredMenus = ConcurrentHashMap<String, BaseMenuFactory>()
	private val openedMenus = ConcurrentHashMap<Inventory, BaseMenu>()
	val menuKeys: ConcurrentHashMap.KeySetView<String, BaseMenuFactory>
		get() = registeredMenus.keys

	fun registerMenu(name: String, menu: BaseMenuFactory) {
		registeredMenus[name] = menu
	}

	fun openMenu(player: HumanEntity, menu: String, previousMenu: BaseMenu?): Boolean {
		return registeredMenus[menu]?.let {
			openMenu(player, it.create(it.title, player, previousMenu, 54))
			true
		} ?: run {
			false
		}
	}

	fun openMenu(player: HumanEntity, menu: BaseMenu) {
		openedMenus.putIfAbsent(menu.inventory, menu)
		player.openInventory(menu.inventory)
	}

	@EventHandler
	private fun onInventoryClick(event: InventoryClickEvent) {
		openedMenus[event.inventory]?.let {
			event.isCancelled = true
			it.onInventoryClick(event)
		}
	}

	@EventHandler
	private fun onInventoryDrag(e: InventoryDragEvent) {
		if (openedMenus.containsKey(e.inventory)) {
			e.isCancelled = true
		}
	}

	@EventHandler
	private fun onInventoryOpen(event: InventoryOpenEvent) {
		openedMenus[event.inventory]?.updateMenu()
	}

	@EventHandler
	private fun onInventoryClose(event: InventoryCloseEvent) {
		openedMenus.remove(event.inventory)
	}
}