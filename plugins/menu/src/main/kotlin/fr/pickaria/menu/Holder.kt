package fr.pickaria.menu

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

internal class Holder(val menu: Menu): InventoryHolder {
	private lateinit var inventory: Inventory

	override fun getInventory(): Inventory = inventory

	fun setInventory(inventory: Inventory) {
		this.inventory = inventory
	}
}