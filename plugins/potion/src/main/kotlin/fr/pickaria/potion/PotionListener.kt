package fr.pickaria.potion

import fr.pickaria.shared.give
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

// TODO: Remove Command, potions should be obtained without a give command
internal class PotionListener : CommandExecutor, TabCompleter, Listener {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		if (sender is Player) {
			args?.getOrNull(0)?.let {
				potionConfig.potions[it]
			}?.let {
				val amount = try {
					args.getOrNull(1)?.toInt() ?: 1
				} catch (_: NumberFormatException) {
					1
				}

				val itemStack = createPotion(it, amount)
				sender.give(itemStack)
			}

			return true
		}

		return false
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		label: String,
		args: Array<out String>?,
	): MutableList<String> {
		return potionConfig.potions.keys.toMutableList()
	}

	@EventHandler
	fun onPlayerConsumeEvent(event: PlayerItemConsumeEvent) {
		if (event.item.type == Material.POTION) {
			val potion = (event.item.itemMeta as PotionMeta)

			potion.persistentDataContainer.get(namespace, PersistentDataType.STRING)?.let {
				potionConfig.potions[it]?.let { config ->
					potionController.addPotionEffect(config, event.player)
					event.setItem(null)
				}
			}
		}
	}
}