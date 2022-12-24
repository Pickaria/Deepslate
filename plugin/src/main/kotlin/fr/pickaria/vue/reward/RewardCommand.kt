package fr.pickaria.vue.reward

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toController
import fr.pickaria.shared.give
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player

@CommandAlias("reward")
@CommandPermission("pickaria.command.reward")
class RewardCommand : BaseCommand() {
	companion object {
		fun setupContext(
			commandContexts: CommandContexts<BukkitCommandExecutionContext>,
			commandCompletions: CommandCompletions<BukkitCommandCompletionContext>
		) {
			commandContexts.registerContext(Reward::class.java) {
				val arg = it.popFirstArg()

				rewardConfig.rewards[arg] ?: throw InvalidCommandArgument("Potion of type '$arg' does not exists.")
			}

			commandCompletions.registerCompletion("reward") {
				rewardConfig.rewards.keys
			}
		}
	}

	@Default
	@Syntax("<reward type> [amount]")
	@CommandCompletion("@reward")
	fun onCommand(sender: Player, @Optional reward: Reward?, @Default("1") amount: Int) {
		if (reward == null) {
			sender open "reward"
			return
		}

		if (!reward.purchasable) {
			throw InvalidCommandArgument(rewardConfig.cantPurchaseReward)
		}

		val totalKeys = (reward.keys * amount).toDouble()
		val totalShards = (reward.shards * amount).toDouble()

		if (!sender.has(Key, totalKeys) || !sender.has(Shard, totalShards)) {
			throw InvalidCommandArgument(rewardConfig.notEnoughMoney)
		}

		val itemStack = reward.toController().create(amount)
		val keyResponse = sender.withdraw(Key, totalKeys)
		val shardResponse = sender.withdraw(Shard, totalShards)

		if (keyResponse.type == EconomyResponse.ResponseType.SUCCESS && shardResponse.type == EconomyResponse.ResponseType.SUCCESS) {
			sender.give(itemStack)
		}
	}
}