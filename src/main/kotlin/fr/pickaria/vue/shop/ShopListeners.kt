package fr.pickaria.vue.shop

import fr.pickaria.controller.economy.*
import fr.pickaria.menu.open
import fr.pickaria.model.shop.menuNamespace
import fr.pickaria.model.shop.shopConfig
import fr.pickaria.spawner.event.*
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

internal class ShopListeners : Listener {
	private fun Player.hasAll(ingredients: List<ItemStack>): Boolean {
		for (ingredient in ingredients) {
			if (!(this has ingredient)) {
				return false
			}
		}

		return true
	}

	private fun Inventory.removeCurrencies() {
		contents.forEach {
			if (it?.isCurrency() == true) {
				it.amount = 0
				removeItem(it)
			}
		}
	}

	@EventHandler
	fun onPlayerSelectTrade(event: PlayerSelectTradeEvent) {
		with(event) {
			val hasAll = player.hasAll(selectedRecipe.ingredients)

			inventory.removeCurrencies()
			player.updateInventory()

			if (hasAll) {
				// Force set the item into the trade view
				selectedRecipe.ingredients.forEachIndexed { index, ingredient ->
					ingredient.currency?.let {
						inventory.setItem(index, ingredient)
						player.playSound(shopConfig.tradeSelectSound)
					}
				}
			}
		}
	}

	@EventHandler
	fun onPlayerBuy(event: PlayerBuyEvent) {
		with(event) {
			val hasAll = player.hasAll(trade.ingredients)

			if (hasAll) {
				trade.ingredients.forEach {
					player withdraw it
				}

				inventory.getItem(2)?.let {
					val response = player deposit it
					if (response.type == EconomyResponse.ResponseType.SUCCESS) {
						inventory.removeItem(trade.result)
						inventory.clear()
					}
				}

				player.playSound(shopConfig.tradeSound)
				player.world.spawnParticle(Particle.END_ROD, player.location, 30, 1.0, 1.0, 1.0, 0.0)
			} else {
				result = Event.Result.DENY
				isCancelled = true
			}
		}
	}

	@EventHandler
	fun onPlayerClickOnVillager(event: PlayerInteractAtEntityEvent) {
		with(event) {
			rightClicked.persistentDataContainer.get(menuNamespace, PersistentDataType.STRING)?.let {
				if (player open it) {
					isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onPlayerOpenShop(event: PlayerOpenShopEvent) {
		// TODO: Adjust max uses
		event.player.playSound(shopConfig.openSound)
	}

	/**
	 * Prevents the player from clicking in other slots than result if the clicked item is a currency.
	 */
	@EventHandler
	fun onShopClickEvent(event: ShopClickEvent) {
		event.result = Event.Result.DENY
		event.isCancelled = true
	}

	/**
	 * Prevents items from being dropped by the custom inventories.
	 */
	@EventHandler
	fun onPlayerCloseShop(event: PlayerCloseShopEvent) {
		with(event) {
			inventory.removeCurrencies()
			player.playSound(shopConfig.closeSound)
		}
	}
}
