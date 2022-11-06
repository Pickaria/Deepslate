package fr.pickaria.shard

import io.papermc.paper.event.player.PlayerPurchaseEvent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.block.EnderChest
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal class ShopListeners : Listener {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)
		private val menus: MutableList<InventoryView> = mutableListOf() // FIXME: Possible memory leak
	}

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		val ingredient = recipe.ingredients.first()

		if (ingredient.isShard() && menus.contains(event.view)) {
			val price = ingredient.amount.toDouble()

			if (economy.has(event.whoClicked as OfflinePlayer, price)) {
				event.inventory.setItem(0, ingredient)
				event.whoClicked.playSound(shopConfig.tradeSelectSound)
				// FIXME: Reopen inventory to update `maxUses`?
			} else {
				// If the player doesn't have enough money, prevent the trade and clears previous trade
				event.inventory.setItem(0, null)
				event.result = Event.Result.DENY
				event.isCancelled = true
			}
		}
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerPurchaseEvent) {
		event.trade.let {
			val ingredient = it.ingredients.first()

			if (ingredient.isShard()) {
				val price = ingredient.amount.toDouble()

				if (economy.has(event.player, price)) {
					economy.withdrawPlayer(event.player, price)
					event.player.playSound(shopConfig.tradeSound)
				} else {
					// If the player doesn't have enough money, prevent the trade
					event.isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onChestOpened(event: PlayerInteractEvent) {
		event.clickedBlock?.let { block ->
			if (block.type == Material.ENDER_CHEST && block.state is EnderChest) {
				val state = (block.state as EnderChest)
				val data = state.persistentDataContainer.get(namespace, PersistentDataType.BYTE)

				// Check if chest is a Shard shop
				if (data == (1).toByte()) {
					val player = event.player

					if (!player.isSneaking) {
						createChestMerchant(player)?.let {
							menus.add(it)

							player.playSound(shopConfig.openSound)
							player.addPotionEffect(openPotionEffect)
							player.world.spawnParticle(Particle.END_ROD, block.location, 30, 1.0, 1.0, 1.0, 0.0)

							event.isCancelled = true
						}
					}
				}
			}
		}
	}

	/**
	 * Prevents the player from clicking in other slots than result.
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.clickedInventory?.type === InventoryType.MERCHANT && menus.contains(event.view) && event.slotType != InventoryType.SlotType.RESULT) {
			event.result = Event.Result.DENY
			event.isCancelled = true
		}
	}

	/**
	 * Prevents items from being dropped by the custom inventories.
	 */
	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		if (event.inventory.type === InventoryType.MERCHANT && menus.contains(event.view)) {
			event.inventory.clear()
			event.player.removePotionEffect(openPotionEffectType)
			event.player.playSound(shopConfig.closeSound)
			menus.remove(event.view)
		}
	}
}
