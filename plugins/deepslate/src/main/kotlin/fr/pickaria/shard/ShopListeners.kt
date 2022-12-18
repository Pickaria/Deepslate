package fr.pickaria.shard

import fr.pickaria.economy.*
import fr.pickaria.spawner.event.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import fr.pickaria.Config
import net.milkbowl.vault.economy.EconomyResponse

internal class ShopListeners : Listener, CurrencyExtensions(Shard, Credit, Key) {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)
	}

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
	fun onTradeSelected(event: PlayerSelectTradeEvent) {
		with(event) {
			val hasAll = player.hasAll(selectedRecipe.ingredients)

			inventory.removeCurrencies() // FIXME: Prevent currencies from ending in player's inventory

			if (hasAll) {
				// Force set the item into the trade view
				selectedRecipe.ingredients.forEachIndexed { index, ingredient ->
					ingredient.currency?.let {
						inventory.setItem(index, ingredient)
						player.playSound(Config.tradeSelectSound)
					}
				}
			}
		}
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerBuyEvent) {
		with(event) {
			val hasAll = player.hasAll(trade.ingredients)

			if (hasAll) {
				trade.ingredients.forEach {
					player withdraw it
				}

				val response = player deposit trade.result
				if (response.type == EconomyResponse.ResponseType.SUCCESS) {
					inventory.removeItem(trade.result)
					inventory.clear()
					player.updateInventory()
					result = Event.Result.DENY
					isCancelled = true
				}

				player.playSound(Config.tradeSound)
				player.world.spawnParticle(Particle.END_ROD, player.location, 30, 1.0, 1.0, 1.0, 0.0)
			} else {
				result = Event.Result.DENY
				isCancelled = true
			}
		}
	}

	@EventHandler
	fun onChestOpened(event: PlayerOpenShopEvent) {
		// TODO: Adjust max uses
		event.player.playSound(Config.openSound)
		event.player.addPotionEffect(openPotionEffect)
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
	fun onInventoryClose(event: PlayerCloseShopEvent) {
		with(event) {
			inventory.removeCurrencies()
			player.removePotionEffect(openPotionEffectType)
			player.playSound(Config.closeSound)
		}
	}
}