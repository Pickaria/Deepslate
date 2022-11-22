package fr.pickaria.reward

import fr.pickaria.economy.Key
import fr.pickaria.economy.Shard
import fr.pickaria.economy.has
import fr.pickaria.economy.withdraw
import fr.pickaria.menu.open
import fr.pickaria.shared.give
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener

internal class RewardCommand : CommandExecutor, Listener, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			if (args.isEmpty()) {
				sender open "reward"
				return true
			}

			val key = args.first()
			val reward: RewardConfig = Config.rewards[key]!!

			if (!reward.purchasable) {
				sender.sendMessage(Config.cantPurchaseReward)
				return true
			}

			val amount = try {
				args.getOrNull(1)?.toInt() ?: 1
			} catch (_: NumberFormatException) {
				1
			}

			val totalKeys = (reward.keys * amount).toDouble()
			val totalShards = (reward.shards * amount).toDouble()

			createReward(key, amount)?.let {
				if (sender.has(Key, totalKeys) && sender.has(Shard, totalShards)) {
					val keyResponse = sender.withdraw(Key, totalKeys)
					val shardResponse = sender.withdraw(Shard, totalShards)

					if (keyResponse.type == EconomyResponse.ResponseType.SUCCESS && shardResponse.type == EconomyResponse.ResponseType.SUCCESS) {
						sender.give(it)
					}
				} else {
					sender.sendMessage(Config.notEnoughMoney)
				}
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