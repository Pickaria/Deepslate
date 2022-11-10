package fr.pickaria.reward

import fr.pickaria.economy.has
import fr.pickaria.economy.withdraw
import fr.pickaria.menu.open
import net.kyori.adventure.text.format.TextDecoration
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

internal class RewardCommand : CommandExecutor, Listener, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			if (args.isEmpty()) {
				sender open "reward"
				return true
			}

			val key = args.first()
			val reward: RewardConfig = Config.rewards[key]!!
			val amount = try {
				args.getOrNull(1)?.toInt() ?: 1
			} catch (_: NumberFormatException) {
				1
			}

			val totalPrice = reward.price * amount
			if (sender has totalPrice) {
				val response = sender withdraw totalPrice

				if (response.type == EconomyResponse.ResponseType.SUCCESS) {
					val item = ItemStack(reward.material, amount)

					item.editMeta {
						it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS)
						it.displayName(reward.name.decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE))
						it.persistentDataContainer.set(namespace, PersistentDataType.STRING, key)
					}

					sender.inventory.addItem(item)
				}
			} else {
				sender.sendMessage(Config.notEnoughMoney)
			}
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