package fr.pickaria.shard

import io.papermc.paper.event.player.PlayerPurchaseEvent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.block.EnderChest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal class ShopListeners : Listener {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)

	}

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		event.inventory.setItem(0, recipe.ingredients.first())
		event.whoClicked.playSound(shopConfig.tradeSelectSound)
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerPurchaseEvent) {
		event.trade.let {
			val price = it.ingredients[0].amount
			economy.withdrawPlayer(event.player as OfflinePlayer, price.toDouble())
			event.player.playSound(shopConfig.tradeSound)
		}
	}

	@EventHandler
	fun onChestOpened(event: PlayerInteractEvent) {
		event.clickedBlock?.let { block ->
			if (block.type == Material.ENDER_CHEST && block.state is EnderChest) {
				val state = (block.state as EnderChest)
				val data = state.persistentDataContainer.get(namespace, PersistentDataType.BYTE)

				if (data == (1).toByte()) {
					val player = event.player
					if (!player.isSneaking) {
						createChestMerchant(player)

						player.playSound(shopConfig.openSound)
						player.addPotionEffect(openPotionEffect)
						player.world.spawnParticle(Particle.END_ROD, block.location, 30, 1.0, 1.0, 1.0, 0.0)

						event.isCancelled = true
					}
				}
			}
		}
	}

	/**
	 * Prevents items from being dropped by the custom inventories.
	 */
	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		// TODO: Identify inventories without using `event.inventory.type == InventoryType.XXX`
		if (event.inventory.type === InventoryType.MERCHANT) {
			event.inventory.clear()
			event.player.removePotionEffect(openPotionEffectType)
			event.player.playSound(shopConfig.closeSound)
		} else if (event.inventory.type == InventoryType.GRINDSTONE) {
			(event.inventory as GrindstoneInventory).result = null
		}
	}
}
