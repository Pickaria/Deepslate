package fr.pickaria.vue.reward

import fr.pickaria.controller.economy.currency
import fr.pickaria.controller.economy.deposit
import fr.pickaria.controller.economy.silentDeposit
import fr.pickaria.controller.reward.RewardHolder
import fr.pickaria.model.economy.Currency
import fr.pickaria.model.economy.toController
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.rewardNamespace
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.loot.LootContext
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.*

internal class RewardListeners : Listener {
	@EventHandler
	fun onRewardOpen(event: PlayerInteractEvent) {
		with(event) {
			if (action.isLeftClick) return@with

			item?.let { item ->
				item.itemMeta?.persistentDataContainer?.get(rewardNamespace, PersistentDataType.STRING)?.let {
					try {
						rewardConfig.rewards[it]
					} catch (_: IllegalArgumentException) {
						null
					}
				}?.let {
					val holder = RewardHolder()
					val inventory = Bukkit.createInventory(
						holder,
						InventoryType.DROPPER,
						item.itemMeta.displayName() ?: Component.empty()
					)
					holder.inventory = inventory

					// Chest meta
					val luck = player.getPotionEffect(PotionEffectType.LUCK)?.amplifier ?: 0

					val lootContext = LootContext.Builder(player.location)
						.luck(luck.toFloat())
						.lootedEntity(player)
						.killer(player)
						.lootingModifier(LootContext.DEFAULT_LOOT_MODIFIER)
						.build()

					it.lootTable.fillInventory(inventory, Random(), lootContext)

					player.playSound(rewardConfig.rewardOpenSound)
					player.openInventory(inventory)

					isCancelled = true

					item.amount -= 1
				}
			}
		}
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		with(event) {
			if (inventory.holder is RewardHolder) {
				currentItem?.let {
					(whoClicked as Player) deposit it
				}
			}
		}
	}

	@EventHandler
	fun onRewardClose(event: InventoryCloseEvent) {
		with(event) {
			if (inventory.holder is RewardHolder) {
				val deposited = mutableMapOf<Currency, Double>()
				val contents = inventory.contents.filterNotNull()

				contents.forEach {
					it.currency?.let { currency ->
						val response = (player as OfflinePlayer) silentDeposit it
						val previousAmount = deposited.getOrDefault(currency.model, 0.0)
						deposited[currency.model] = previousAmount + response.amount
					}
				}

				// Merge collect messages
				deposited.forEach { (currency, amount) ->
					currency.toController().message(player as Player, amount)
				}

				player.give(*contents.toTypedArray())
				player.playSound(rewardConfig.rewardCloseSound)
			}
		}
	}
}