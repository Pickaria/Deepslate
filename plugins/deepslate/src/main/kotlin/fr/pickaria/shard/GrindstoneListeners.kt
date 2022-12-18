package fr.pickaria.shard

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.Config
import fr.pickaria.artefact.artefact
import fr.pickaria.economy.Credit
import fr.pickaria.economy.CurrencyExtensions
import fr.pickaria.economy.Shard
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack

internal class GrindstoneListeners : Listener, CurrencyExtensions(Credit, Shard) {
	private fun getResult(itemStack: ItemStack?): ItemStack? = itemStack?.let {
		if (it.amount == 1) it.artefact else null
	}?.let {
		val amount = (it.value * Config.grindLoss)
		if (amount < 1) {
			Credit.item((amount * 64).toInt(), Config.grindCoinValue)
		} else {
			Shard.item(amount.toInt())
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
				event.inventory.location?.world?.playSound(Config.grindPlaceSound)
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
		with(event) {
			if (inventory.type == InventoryType.GRINDSTONE && slotType == InventoryType.SlotType.RESULT) {
				currentItem?.let {
					val player = whoClicked as Player
					val response = player deposit it

					if (response.type == EconomyResponse.ResponseType.SUCCESS) {
						inventory.removeItem(it)
						inventory.clear()
						player.updateInventory()
						result = Event.Result.DENY
						isCancelled = true
					}
				}
			}
		}
	}
}