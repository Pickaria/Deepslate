package fr.pickaria.shop

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.floor

class TestMenu : Listener {
	var chest: Chest? = null

	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)
		val price = recipe.ingredients.first().amount

		economy.withdrawPlayer(event.whoClicked as OfflinePlayer, price.toDouble())
		chest!!.inventory.removeItemAnySlot(recipe.result)
		event.whoClicked.inventory.addItem(recipe.result)

		createChestMerchant(event.whoClicked as Player, chest!!.inventory)
	}

	@EventHandler
	fun onInventoryDrag(event: InventoryClickEvent) {
		event.clickedInventory?.let { inventory ->
			if (inventory.type === InventoryType.MERCHANT) {
				if (event.slot == 2) {
					// Sell current item
					inventory.getItem(0)?.let {
						chest!!.inventory.addItem(it)
						economy.depositPlayer(event.whoClicked as OfflinePlayer, it.amount.toDouble())
						inventory.clear()
						inventory.contents = emptyArray()
						createChestMerchant(event.whoClicked as Player, chest!!.inventory)
					}
				} else if (event.slot == 0) {
					if (event.action == InventoryAction.PLACE_ALL) {
						event.cursor?.let {
							val price = (floor(Math.random() * 64) + 1).toInt()

							val contents = inventory.contents

							contents[0] = it
							contents[1] = ItemStack(Material.SUNFLOWER, price)

							inventory.contents = contents

							it.amount = 0
						}
					} else if (event.action == InventoryAction.PICKUP_ALL) {
						event.cursor = inventory.getItem(0)
						inventory.clear()
					}
				}

				event.isCancelled = true
			}
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
}
