package fr.pickaria.shop

import io.papermc.paper.event.player.PlayerPurchaseEvent
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
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
	@EventHandler
	fun onTradeSelected(event: TradeSelectEvent) {
		val recipe = event.merchant.getRecipe(event.index)

		// Force set the item into the trade view
		event.inventory.setItem(0, recipe.ingredients.first())
		event.whoClicked.playSound(Sound.sound(Key.key("block.large_amethyst_bud.place"), Sound.Source.MASTER, 1f, 1f))
	}

	@EventHandler
	fun onPlayerTrade(event: PlayerPurchaseEvent) {
		event.trade.let {
			val price = it.ingredients[0].amount
			economy.withdrawPlayer(event.player as OfflinePlayer, price.toDouble())
			event.player.playSound(Sound.sound(Key.key("block.large_amethyst_bud.break"), Sound.Source.MASTER, 1f, 1f))
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

						player.playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.MASTER, 1f, 1f))
						player.addPotionEffect(
							PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, true, false, false)
						)
						player.world.spawnParticle(Particle.END_ROD, player.location, 100, 3.0, 3.0, 3.0, 0.0)

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
			event.player.removePotionEffect(PotionEffectType.BLINDNESS)
			event.player.playSound(Sound.sound(Key.key("block.amethyst_block.chime"), Sound.Source.MASTER, 1f, 1f))
			event.player.playSound(Sound.sound(Key.key("block.ender_chest.close"), Sound.Source.MASTER, 1f, 1f))
		} else if (event.inventory.type == InventoryType.GRINDSTONE) {
			(event.inventory as GrindstoneInventory).result = null
		}
	}
}
