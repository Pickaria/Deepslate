package fr.pickaria.reward

import fr.pickaria.shard.creditShard
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.*

internal class CrateCommand : CommandExecutor, Listener, TabCompleter {
	inner class CrateHolder : InventoryHolder {
		private lateinit var inventory: Inventory

		fun setInventory(inventory: Inventory) {
			this.inventory = inventory
		}

		override fun getInventory(): Inventory = inventory
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val reward: Rewards = Rewards.valueOf(args.first())
			val amount = try {
				args.getOrNull(1)?.toInt() ?: 1
			} catch (_: NumberFormatException) {
				1
			}

			val item = ItemStack(reward.material, amount)

			item.editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)

				it.displayName(
					Component.text(reward.title, reward.rarity.color, TextDecoration.BOLD)
						.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
				)

				it.persistentDataContainer.set(namespace, PersistentDataType.STRING, reward.name)
			}

			sender.inventory.addItem(item)
		}

		return true
	}

	@EventHandler
	fun onCrateOpen(event: PlayerInteractEvent) = with(event) {
		if (action.isLeftClick) return@with

		item?.let { item ->
			item.itemMeta?.persistentDataContainer?.get(namespace, PersistentDataType.STRING)?.let {
				try {
					Rewards.valueOf(it)
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

				Bukkit.getLootTable(it.lootTable)?.fillInventory(inventory, Random(), lootContext)

				player.playSound(Sound.sound(Key.key("item.bundle.insert"), Sound.Source.MASTER, 1F, 1F))
				player.openInventory(inventory)

				isCancelled = true

				item.amount -= 1
			}
		}
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) = with(event) {
		if (inventory.holder is CrateHolder) {
			currentItem?.let {
				creditShard(it, whoClicked as Player)
			}
		}
	}


	@EventHandler
	fun onCrateClosed(event: InventoryCloseEvent) = with(event) {
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

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?
	): MutableList<String> = mutableListOf("COMMON", "UNCOMMON", "RARE", "EPIC")
}