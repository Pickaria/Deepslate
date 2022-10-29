package fr.pickaria.menu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory

abstract class BaseMenu(
	title: Component,
	protected val opener: HumanEntity,
	private val previousMenu: BaseMenu? = null,
	size: Int = 54
) {
	val inventory: Inventory = Bukkit.createInventory(null, size, title)

	abstract class Factory(val title: Component, val icon: Material = Material.AIR, val description: List<Component> = listOf()) {
		abstract fun create(
			opener: HumanEntity,
			previousMenu: BaseMenu? = null,
		): BaseMenu
	}

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
		val fill = MenuItem.build {
			material = fillMaterial
		}

		inventory.clear()

		initMenu()

		for (i in page * pageSize until (page + 1) * pageSize) {
			val menuItemStack = menuItemStacks[i] ?: fill
			val slot = i - page * 45
			inventory.setItem(slot, menuItemStack.itemStack)
		}

		// Pagination items
		if (page > 0) {
			inventory.setItem(
				previousPageSlot,
				createMenuItem(Material.ARROW, Component.text("Page précédente"), listOf(Component.text("Clic-gauche pour retourner à la page précédente")))
			)
		}

		previousMenu?.let {
			inventory.setItem(
				menuBackSlot,
				createMenuItem(Material.ARROW, Component.text("Retour"), listOf(Component.text("Clic-gauche pour retourner au menu précédent")))
			)
		} ?: run {
			inventory.setItem(
				menuBackSlot,
				createMenuItem(Material.BARRIER, Component.text("Fermer"), listOf(Component.text("Clic-gauche pour fermer le menu.")))
			)
		}

		if (last >= (page + 1) * pageSize) {
			inventory.setItem(
				nextPageSlot,
				createMenuItem(Material.ARROW, Component.text("Page suivante"), listOf(Component.text("Clic-gauche pour aller à la page suivante")))
			)
		}
	}

	protected fun setMenuItem(block: MenuItem.() -> Unit): BaseMenu {
		val menuItem = MenuItem.build(block)
		val pos = menuItem.y * 9 + menuItem.x
		if (pos > last) last = pos
		menuItemStacks[pos] = menuItem
		return this
	}

	// Check for clicks on items
	fun onInventoryClick(event: InventoryClickEvent) {
		val clickedItem = event.currentItem

		// verify current item is not null
		if (clickedItem == null || clickedItem.type.isAir) return
		val slot = event.rawSlot
		val menuItemStack = menuItemStacks[slot]
		if (slot < pageSize && menuItemStack != null) {
			if (event.isLeftClick) {
				menuItemStack.leftClick?.invoke(event)
			}
			if (event.isRightClick) {
				menuItemStack.rightClick?.invoke(event)
			}
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