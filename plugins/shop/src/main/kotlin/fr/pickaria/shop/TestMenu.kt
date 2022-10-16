package fr.pickaria.shop

import io.papermc.paper.event.player.PlayerPurchaseEvent
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect

class TestMenu : Listener {
	var chest: Chest? = null

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		event.inventory.setItem(0, recipe.ingredients.first())
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerPurchaseEvent) {
		event.trade.let {
			val price = it.ingredients[0].amount
			economy.withdrawPlayer(event.player as OfflinePlayer, price.toDouble())
			chest!!.inventory.removeItemAnySlot(it.result)
		}
	}

	@EventHandler
	fun onChestOpened(event: PlayerInteractEvent) {
		chest = event.clickedBlock?.let { block ->
			if (!event.player.isSneaking && block.type == Material.CHEST && block.state is Chest) {
				(block.state as? Chest)?.let {
					createChestMerchant(event.player, it.inventory)
					event.isCancelled = true
					it
				}
			} else {
				null
			}
		}
	}

	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		if (event.inventory.type === InventoryType.MERCHANT) {
			event.inventory.clear()
		}
	}
}
