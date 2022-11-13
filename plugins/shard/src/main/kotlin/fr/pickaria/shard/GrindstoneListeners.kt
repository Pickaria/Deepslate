package fr.pickaria.shard

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.artefact.getArtefactConfig
import fr.pickaria.economy.Credit
import fr.pickaria.economy.CurrencyExtensions
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.InventoryType.SlotType
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack

internal class GrindstoneListeners : Listener, CurrencyExtensions(Shard) {
	private fun getResult(itemStack: ItemStack?): ItemStack? = itemStack?.let {
		if (it.amount == 1) getArtefactConfig(it) else null
	}?.let {
		val amount = (it.value * shopConfig.grindLoss)
		if (amount < 1) {
			Credit.item((amount * 64).toInt(), shopConfig.grindCoinValue)
		} else {
			Shard.item(it.value)
		}
	}

	/**
	 * Sets Shards as a result of Grindstone if the deposited item is an artefact.
	 */
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.GRINDSTONE) {
			val inventory = (event.inventory as GrindstoneInventory)

			val upper = getResult(inventory.upperItem)
			val lower = getResult(inventory.lowerItem)

			val result: ItemStack? = if (inventory.upperItem == inventory.lowerItem && inventory.upperItem != null) {
				// If both items are the same
				upper
			} else if ((inventory.upperItem != null).xor(inventory.lowerItem != null)) {
				// If one of the items is different
				upper ?: lower
			} else {
				// Else, if both items are null
				null
			}

			result?.let {
				event.inventory.location?.world?.playSound(shopConfig.grindPlaceSound)
				event.result = it
			}
		}
	}

	/**
	 * Pickup Shards from Grindstone and prevent Shards from ending in player's inventory.
	 * Deposit money on the player's account and clear the inventory.
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		event.currentItem?.let { item ->
			if (item.isCurrency()) {
				val inventory = event.inventory

				if (inventory.type == InventoryType.GRINDSTONE && event.slotType == SlotType.RESULT) {
					val player = event.whoClicked as Player

					// Add Shards to account and clears the Grindstone's inventory
					player deposit item
					inventory.clear()

					// Just some feedback
					player.playSound(shopConfig.grindSound)
					inventory.location?.let {
						it.world.spawnParticle(Particle.END_ROD, it.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
					}

					// Prevent the item from being moved
					event.result = Event.Result.DENY
					event.isCancelled = true
				}

				// Remove Shards from any inventories
				item.amount = 0
			}
		}
	}
}