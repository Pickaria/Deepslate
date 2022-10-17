package fr.pickaria.shop

import com.destroystokyo.paper.event.inventory.PrepareResultEvent
import fr.pickaria.artefact.getArtefact
import io.papermc.paper.event.player.PlayerPurchaseEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class TestMenu : Listener {
	var chest: Chest? = null

	// TODO: Get list of items to sell from a config file instead of a chest
	// TODO: Set price of each items in the config file
	// TODO: Identify inventories without using `event.inventory.type == InventoryType.XXX`

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		event.inventory.setItem(0, recipe.ingredients.first())
		event.whoClicked.playSound(Sound.sound(Key.key("block.large_amethyst_bud.place"), Sound.Source.MASTER, 1f, 1f))
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerPurchaseEvent) {
		event.trade.let {
			val price = it.ingredients[0].amount
			economy.withdrawPlayer(event.player as OfflinePlayer, price.toDouble())
			chest!!.inventory.removeItemAnySlot(it.result)
			event.player.playSound(Sound.sound(Key.key("block.large_amethyst_bud.break"), Sound.Source.MASTER, 1f, 1f))
		}
	}

	@EventHandler
	fun onChestOpened(event: PlayerInteractEvent) {
		chest = event.clickedBlock?.let { block ->
			val player = event.player
			if (!player.isSneaking && block.type == Material.CHEST && block.state is Chest) {
				(block.state as? Chest)?.let {
					createChestMerchant(player, it.inventory)

					player.playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.MASTER, 1f, 1f))
					player.addPotionEffect(
						PotionEffect(
							PotionEffectType.BLINDNESS,
							Integer.MAX_VALUE,
							1,
							true,
							false,
							false
						)
					)
					player.world.spawnParticle(Particle.END_ROD, player.location, 100, 3.0, 3.0, 3.0, 0.0)

					event.isCancelled = true
					it
				}
			} else {
				null
			}
		}

		// TODO: Add effects when player opens Grindstone
	}

	/**
	 * Sets Pickarite as a result of Grindstone if the deposited item is an artefact
	 */
	@EventHandler
	fun onPrepareResult(event: PrepareResultEvent) {
		if (event.inventory.type == InventoryType.GRINDSTONE) {
			val inventory = (event.inventory as GrindstoneInventory)
			// TODO: Handle lowerItem
			// TODO: Add sound
			inventory.upperItem?.let { getArtefact(it) }?.let {
				event.result = createPickarite(it.value)
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
			event.currentItem?.let {
				if (isPickarite(it)) {
					economy.depositPlayer(event.whoClicked as OfflinePlayer, it.amount.toDouble())
					event.inventory.clear()
					event.isCancelled = true
				}
			}
		}
	}

	/**
	 * Prevents items from being dropped by the custom inventories.
	 */
	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		if (event.inventory.type === InventoryType.MERCHANT || event.inventory.type == InventoryType.GRINDSTONE) {
			event.inventory.clear()
			event.player.removePotionEffect(PotionEffectType.BLINDNESS)
			event.player.playSound(Sound.sound(Key.key("block.amethyst_block.chime"), Sound.Source.MASTER, 1f, 1f))
			event.player.playSound(Sound.sound(Key.key("block.ender_chest.close"), Sound.Source.MASTER, 1f, 1f))
		}
	}
}
