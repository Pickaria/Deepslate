package fr.pickaria.shard

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.artefact.getArtefactConfig
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack

internal class GrindstoneListeners : Listener {
	private fun getResult(itemStack: ItemStack?): ItemStack? = itemStack?.let {
		if (it.amount == 1) getArtefactConfig(it) else null
	}?.let {
		createPickarite(it.value)
	}

	/**
	 * Sets Pickarite as a result of Grindstone if the deposited item is an artefact
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
	 * Pickup Pickarite from Grindstone.
	 * Deposit money on the player's account and clear the inventory.
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.inventory.type == InventoryType.GRINDSTONE && event.slotType == InventoryType.SlotType.RESULT) {
			event.currentItem?.let { itemStack ->
				if (isPickarite(itemStack)) {
					economy.depositPlayer(event.whoClicked as OfflinePlayer, itemStack.amount.toDouble())

					event.whoClicked.playSound(shopConfig.grindSound)
					event.inventory.location?.let {
						it.world.spawnParticle(Particle.END_ROD, it.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
					}

					event.inventory.clear()
					event.currentItem?.amount = 0
					event.isCancelled = true
				}
			}
		}
	}
}