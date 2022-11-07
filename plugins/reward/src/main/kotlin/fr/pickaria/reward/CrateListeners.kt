package fr.pickaria.reward

import fr.pickaria.shard.creditShard
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
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

internal class CrateListeners: Listener {
	@EventHandler
	fun onCrateOpen(event: PlayerInteractEvent) {
		with(event) {
			if (action.isLeftClick) return@with

			item?.let { item ->
				item.itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.STRING)?.let {
					try {
						Config.rewards[it]
					} catch (_: IllegalArgumentException) {
						null
					}
				}?.let {
					val holder = CrateHolder()
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

					player.playSound(Sound.sound(Key.key("item.bundle.insert"), Sound.Source.MASTER, 1F, 1F))
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
			if (inventory.holder is CrateHolder) {
				currentItem?.let {
					creditShard(it, whoClicked as Player)
				}
			}
		}
	}


	@EventHandler
	fun onCrateClosed(event: InventoryCloseEvent) {
		with(event) {
			if (inventory.holder is CrateHolder) {
				val contents = inventory.contents.filterNotNull()
				contents.forEach {
					creditShard(it, player as Player)
				}

				player.inventory.addItem(*contents.toTypedArray()).forEach {
					val location = player.eyeLocation
					val item = location.world.dropItem(location, it.value)
					item.velocity = location.direction.multiply(0.25)
				}
				player.playSound(Sound.sound(Key.key("item.bundle.drop_contents"), Sound.Source.MASTER, 1F, 1F))
			}
		}
	}
}