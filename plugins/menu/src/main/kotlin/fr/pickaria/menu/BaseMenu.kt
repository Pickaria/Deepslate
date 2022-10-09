package fr.pickaria.menu

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import java.util.function.Consumer

abstract class BaseMenu(
	protected val title: String,
	protected val opener: HumanEntity? = null,
	val previousMenu: BaseMenu? = null,
	protected val size: Int = 54
) {
	val inventory: Inventory = Bukkit.createInventory(null, size, Component.text(title))

	private val previousPageSlot = size - 7
	private val menuBackSlot = size - 5
	private val nextPageSlot = size - 3
	private val pageSize = size - 9

	private val menuItemStacks = mutableMapOf<Int, MenuItem>()

	private var page = 0
	private var last = 0

	protected var fillMaterial = Material.AIR

	abstract fun initMenu()

	fun updateMenu() {
		CoroutineScope(Dispatchers.Menus).launch {
			val fill = MenuItem(fillMaterial)

			inventory.clear()

			initMenu()

			for (i in page * pageSize until (page + 1) * pageSize) {
				val menuItemStack: MenuItem = menuItemStacks[i] ?: fill
				val slot = i - page * 45
				inventory.setItem(slot, menuItemStack.itemStack)
			}

			// Pagination items
			if (page > 0) {
				inventory.setItem(
					previousPageSlot,
					createMenuItem(Material.ARROW, "Page précédente", "Clic-gauche pour retourner à la page précédente")
				)
			}

			previousMenu?.let {
				inventory.setItem(
					menuBackSlot,
					createMenuItem(Material.ARROW, "Retour", "Clic-gauche pour retourner au menu précédent")
				)
			} ?: run {
				inventory.setItem(
					menuBackSlot,
					createMenuItem(Material.BARRIER, "Fermer", "Clic-gauche pour fermer le menu.")
				)
			}

			if (last >= (page + 1) * pageSize) {
				inventory.setItem(
					nextPageSlot,
					createMenuItem(Material.ARROW, "Page suivante", "Clic-gauche pour aller à la page suivante")
				)
			}
		}
	}

	protected fun setMenuItem(pos: Int, menuItemStack: MenuItem): BaseMenu {
		if (pos > last) last = pos
		menuItemStacks[pos] = menuItemStack
		return this
	}

	protected fun setMenuItem(x: Int, y: Int, menuItemStack: MenuItem): BaseMenu = setMenuItem(y * 9 + x, menuItemStack)

	// Check for clicks on items
	fun onInventoryClick(event: InventoryClickEvent) {
		val clickedItem = event.currentItem

		// verify current item is not null
		if (clickedItem == null || clickedItem.type.isAir) return
		val slot = event.rawSlot
		val menuItemStack: MenuItem? = menuItemStacks[slot]
		if (slot < pageSize && menuItemStack != null) {
			val callback: Consumer<InventoryClickEvent>? = menuItemStack.callback
			callback?.accept(event)
			return
		}

		// Handle pagination
		if (slot == previousPageSlot) {
			page--
			updateMenu()
		} else if (slot == menuBackSlot) {
			if (previousMenu != null) {
				menuController.openMenu(event.whoClicked, previousMenu)
			} else {
				event.whoClicked.closeInventory()
			}
		} else if (slot == nextPageSlot) {
			page++
			updateMenu()
		}
	}
}