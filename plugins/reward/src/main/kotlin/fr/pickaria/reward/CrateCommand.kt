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
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val key = args.first()
			val reward: RewardConfig = Config.rewards[key]!!
			val amount = try {
				args.getOrNull(1)?.toInt() ?: 1
			} catch (_: NumberFormatException) {
				1
			}

			val item = ItemStack(reward.material, amount)

			item.editMeta {
				it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
				it.displayName(reward.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
				it.persistentDataContainer.set(namespace, PersistentDataType.STRING, key)
			}

			sender.inventory.addItem(item)
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?
	): MutableList<String> = Config.rewards.keys.toMutableList()
}