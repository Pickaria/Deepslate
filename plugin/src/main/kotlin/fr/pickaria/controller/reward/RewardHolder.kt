package fr.pickaria.controller.reward

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

internal class RewardHolder : InventoryHolder {
	private lateinit var inventory: Inventory

	fun setInventory(inventory: Inventory) {
		this.inventory = inventory
	}

	override fun getInventory(): Inventory = inventory
}