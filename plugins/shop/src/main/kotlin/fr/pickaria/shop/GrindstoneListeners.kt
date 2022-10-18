package fr.pickaria.shop

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.artefact.getArtefact
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.GrindstoneInventory

class GrindstoneListeners: Listener {
	// TODO: Add effects when player opens Grindstone

	/**
	 * Sets Pickarite as a result of Grindstone if the deposited item is an artefact
	 */
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.GRINDSTONE) {
			val inventory = (event.inventory as GrindstoneInventory)
			// TODO: Handle lowerItem
			// TODO: Add sound
			var totalValue = 0
			var isArtefact = false

			inventory.upperItem?.let { getArtefact(it) }?.let {
				totalValue += it.value * (inventory.upperItem?.amount ?: 1)
				isArtefact = true
			}
			inventory.lowerItem?.let { getArtefact(it) }?.let {
				totalValue += it.value * (inventory.lowerItem?.amount ?: 1)
				isArtefact = true
			}

			if (totalValue in 1..64) {
				event.result = createPickarite(totalValue)
			} else if (isArtefact) {
				event.result = null
			}
		}
	}

	private fun isPickupAction(action: InventoryAction): Boolean =
		action == InventoryAction.PICKUP_ALL ||
				action == InventoryAction.PICKUP_HALF ||
				action == InventoryAction.PICKUP_ONE ||
				action == InventoryAction.PICKUP_SOME

	/**
	 * Pickup Pickarite from Grindstone.
	 * Deposit money on the player's account and clear the inventory.
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.inventory.type == InventoryType.GRINDSTONE && isPickupAction(event.action)) {
			event.currentItem?.let { itemStack ->
				if (isPickarite(itemStack)) {
					economy.depositPlayer(event.whoClicked as OfflinePlayer, itemStack.amount.toDouble())

					event.whoClicked.playSound(Sound.sound(Key.key("block.amethyst_cluster.break"), Sound.Source.MASTER, 1f, 1f))
					event.inventory.location?.let {
						it.world.spawnParticle(Particle.END_ROD, it.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
					}

					event.inventory.clear()
					event.isCancelled = true
				}
			}
		}
	}
}