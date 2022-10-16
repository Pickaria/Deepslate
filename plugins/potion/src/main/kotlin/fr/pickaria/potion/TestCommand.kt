package fr.pickaria.potion

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType

internal class TestCommand() : CommandExecutor, Listener {
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
				sender.inventory.addItem(itemStack)
			}

			return true
		}

		return false
	}

	@EventHandler
	fun onPlayerConsumeEvent(event: PlayerItemConsumeEvent) {
		if (event.item.type == Material.POTION) {
			val potion = (event.item.itemMeta as PotionMeta)

			potion.persistentDataContainer.get(namespace, PersistentDataType.STRING)?.let {
				potionConfig.potions[it]?.let {
					potionController.addPotionEffect(it, event.player)
				}
			}
		}
	}
}