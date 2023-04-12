package fr.pickaria.model.market

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class SearchHolder : InventoryHolder {
	private lateinit var inventory: Inventory

	fun setInventory(inventory: Inventory) {
		this.inventory = inventory
	}

	override fun getInventory(): Inventory = inventory
}