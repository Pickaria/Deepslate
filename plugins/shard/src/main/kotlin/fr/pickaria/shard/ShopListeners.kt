package fr.pickaria.shard

import fr.pickaria.economy.CurrencyExtensions
import fr.pickaria.economy.balance
import fr.pickaria.economy.has
import fr.pickaria.economy.withdraw
import fr.pickaria.shopapi.event.*
import org.bukkit.Particle
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

internal class ShopListeners : Listener, CurrencyExtensions(Shard) {
	companion object {
		private val openPotionEffectType = PotionEffectType.BLINDNESS
		private val openPotionEffect = PotionEffect(openPotionEffectType, Integer.MAX_VALUE, 1, true, false, false)
	}

	@EventHandler
	fun onTradeSelected(event: PlayerSelectTradeEvent) {
		with(event) {
			// Force set the item into the trade view
			val ingredient = selectedRecipe.ingredients.first()

			if (ingredient.isCurrency()) {
				val price = ingredient.amount.toDouble()

				if (player.has(Shard, price)) {
					inventory.setItem(0, ingredient)
					player.playSound(Config.tradeSelectSound)
					// FIXME: Reopen inventory to update `maxUses`?
				} else {
					// If the player doesn't have enough money, prevent the trade and clears previous trade
					inventory.setItem(0, null)
					result = Event.Result.DENY
					isCancelled = true
				}
			}
		}
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerBuyEvent) {
		with(event) {
			trade.let {
				val ingredient = it.ingredients.first()

				if (ingredient.isCurrency()) {
					val price = ingredient.amount.toDouble()

					if (player.has(Shard, price)) {
						player.withdraw(Shard, price)
						player.playSound(Config.tradeSound)
					} else {
						// If the player doesn't have enough money, prevent the trade
						result = Event.Result.DENY
						isCancelled = true
					}
				}
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
				player.world.spawnParticle(Particle.END_ROD, player.location, 30, 1.0, 1.0, 1.0, 0.0)
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
