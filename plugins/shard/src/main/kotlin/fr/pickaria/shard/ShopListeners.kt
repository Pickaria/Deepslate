package fr.pickaria.shard

import fr.pickaria.economy.CurrencyExtensions
import fr.pickaria.economy.has
import fr.pickaria.economy.withdraw
import io.papermc.paper.event.player.PlayerPurchaseEvent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.block.EnderChest
import org.bukkit.entity.Mob
import org.bukkit.entity.Villager
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.MerchantInventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal class ShopListeners : Listener, CurrencyExtensions(Shard) {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)
	}

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		val ingredient = recipe.ingredients.first()

		if (ingredient.isCurrency()) {
			val price = ingredient.amount.toDouble()

			if ((event.whoClicked as OfflinePlayer).has(Shard, price)) {
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

			if (ingredient.isCurrency()) {
				val price = ingredient.amount.toDouble()

				if (event.player.has(Shard, price)) {
					event.player.withdraw(Shard, price)
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
							event.isCancelled = true
						}
					}
				}
			}
		}
	}

	/**
	 * Prevents the player from clicking in other slots than result if the clicked item is a currency.
	 */
	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		if (event.clickedInventory?.type === InventoryType.MERCHANT && event.slotType != InventoryType.SlotType.RESULT && event.currentItem?.isCurrency() == true) {
			event.result = Event.Result.DENY
			event.isCancelled = true
		}
	}

	/**
	 * Prevents items from being dropped by the custom inventories.
	 */
	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		with(event) {
			if (inventory.type === InventoryType.MERCHANT) {
				inventory.contents.forEach {
					if (it?.isCurrency() == true) {
						inventory.removeItem(it)
					}
				}

				val villager = (inventory as MerchantInventory).merchant as Mob

				if (villager.persistentDataContainer.has(namespace)) {
					player.removePotionEffect(openPotionEffectType)
					player.playSound(shopConfig.closeSound)
				}
			}
		}
	}

	@EventHandler
	fun onInventoryOpen(event: InventoryOpenEvent) {
		with(event) {
			if (inventory.type === InventoryType.MERCHANT) {
				val villager = (inventory as MerchantInventory).merchant as Villager
				if (villager.persistentDataContainer.has(namespace)) {
					player.playSound(shopConfig.openSound)
					player.addPotionEffect(openPotionEffect)
					player.world.spawnParticle(Particle.END_ROD, player.location.toCenterLocation(), 30, 1.0, 1.0, 1.0, 0.0)
				}
			}
		}
	}
}
