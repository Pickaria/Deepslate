package fr.pickaria.shard

import fr.pickaria.economy.*
import fr.pickaria.shopapi.event.*
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal class ShopListeners : Listener, CurrencyExtensions(Shard, Credit) {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)
	}

	private fun Player.hasAll(ingredients: List<ItemStack>): Boolean {
		for (ingredient in ingredients) {
			ingredient.currency?.let { currency ->
				if (!has(currency, ingredient.amount.toDouble())) {
					return false
				}
			}
		}

		return true
	}

	@EventHandler
	fun onTradeSelected(event: PlayerSelectTradeEvent) {
		with(event) {
			val hasAll = player.hasAll(selectedRecipe.ingredients)

			if (hasAll) {
				// Force set the item into the trade view
				selectedRecipe.ingredients.forEachIndexed { index, ingredient ->
					ingredient.currency?.let {
						inventory.setItem(index, ingredient)
						player.playSound(Config.tradeSelectSound)
					}
				}
			} else {
				inventory.clear()
				result = Event.Result.DENY
				isCancelled = true
			}
		}
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerBuyEvent) {
		with(event) {
			val hasAll = player.hasAll(trade.ingredients)

			if (hasAll) {
				trade.ingredients.forEach {
					it.currency?.let { currency ->
						player.withdraw(currency, it.amount.toDouble())
					}
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
		with(event) {
			if (player.balance(Shard) < 1) {
				player.playSound(Config.noShardToTradeSound)
				player.sendMessage(Config.noShardToTrade)
				isCancelled = true
			} else {
				// TODO: Adjust max uses
				player.playSound(Config.openSound)
				player.addPotionEffect(openPotionEffect)
			}
		}
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
			inventory.contents.forEach {
				if (it?.isCurrency() == true) {
					inventory.removeItem(it)
				}
			}

			player.removePotionEffect(openPotionEffectType)
			player.playSound(Config.closeSound)
		}
	}
}
