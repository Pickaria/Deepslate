package fr.pickaria.vue.reward

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.reward.*
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toController
import fr.pickaria.shared.give
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
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
	@Subcommand("menu")
	fun onDefault(sender: Player) {
		sender open "reward"
	}

	@CommandCompletion("@reward")
	@Description("Permet d'acheter une récompense.")
	@Subcommand("buy")
	fun onBuy(sender: Player, reward: Reward, @Default("1") amount: Int) {
		if (!reward.purchasable) {
			throw ConditionFailedException(rewardConfig.cantPurchaseReward)
		}

		val totalKeys = (reward.keys * amount)
		val totalShards = (reward.shards * amount)

		if (!sender.has(Key, totalKeys) || !sender.has(Shard, totalShards)) {
			throw ConditionFailedException(rewardConfig.notEnoughMoney)
		}

		val itemStack = reward.toController().create(amount)
		val keyResponse = sender.withdraw(Key, totalKeys)
		val shardResponse = sender.withdraw(Shard, totalShards)

		if (keyResponse.type == EconomyResponse.ResponseType.SUCCESS && shardResponse.type == EconomyResponse.ResponseType.SUCCESS) {
			sender.give(itemStack)
		}
	}

	@Subcommand("claim")
	@Description("Permet de récupérer une récompense journalière.")
	fun onClaim(sender: Player) {
		sender.dailyReward.let {
			it.rewards().getOrNull(it.collected())?.let { reward ->
				if (it.collect()) {
					val item = reward.toController().create()
					sender.give(item)

					val feedback = reward.name.color(NamedTextColor.GOLD).appendSpace()
						.append(Component.text("récupérée !", NamedTextColor.GRAY))
					sender.sendMessage(feedback)
				} else {
					throw ConditionFailedException("Il vous manque ${rewardConfig.dailyPointsToCollect - it.dailyPoints()} points.")
				}
			} ?: throw ConditionFailedException("Plus aucune récompense pour aujourd'hui.")
		}
	}
}