package fr.pickaria.vue.reward

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.reward.*
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Key
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.reward.DailyReward
import fr.pickaria.model.reward.Reward
import fr.pickaria.model.reward.rewardConfig
import fr.pickaria.model.reward.toController
import fr.pickaria.shared.give
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone.Companion.currentSystemDefault
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

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

	@Subcommand("test")
	fun onTest() {
		val today = Clock.System.todayIn(currentSystemDefault())
		val yesterday = today.minus(1, DateTimeUnit.DAY)

		// The two dates are different
		assert(today != yesterday)
		assert(today.toString() != yesterday.toString())

		transaction {
			// Just collected the first 10 points ever
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = today
				dailyPoints = 10
			}

			assert(dailyReward.rewardCount(today) == 1)
			assert(dailyReward.remainingToCollect(today) == 1)
			assert(dailyReward.canCollect(today) == 0)
			assert(dailyReward.streak(today) == 0)
			assert(dailyReward.collected(today) == 0)
			assert(dailyReward.dailyPoints(today) == 10)

			dailyReward.delete()
		}

		transaction {
			// Has collected points yesterday
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = yesterday
				dailyPoints = 10
			}

			assert(dailyReward.remainingToCollect(today) == 1)
			assert(dailyReward.canCollect(today) == 0)
			assert(dailyReward.streak(today) == 0)
			assert(dailyReward.collected(today) == 0)
			assert(dailyReward.dailyPoints(today) == 0)

			dailyReward.delete()
		}

		transaction {
			// Has collected reward yesterday and points today
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = today
				lastCollectedDate = yesterday
				collectedToday = 1
				dailyPoints = 10
			}

			assert(dailyReward.remainingToCollect(today) == 1)
			assert(dailyReward.canCollect(today) == 0)
			assert(dailyReward.streak(today) == 0)
			assert(dailyReward.collected(today) == 0)
			assert(dailyReward.dailyPoints(today) == 10)

			dailyReward.delete()
		}

		transaction {
			// Has a streak from yesterday
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = yesterday
				lastCollectedDate = yesterday
				collectedToday = 1
				dailyPoints = 10
				streak = 1
			}

			assert(dailyReward.remainingToCollect(today) == 1)
			assert(dailyReward.canCollect(today) == 0)
			assert(dailyReward.streak(today) == 1)
			assert(dailyReward.collected(today) == 0)
			assert(dailyReward.dailyPoints(today) == 10)

			dailyReward.delete()
		}

		transaction {
			// Has collected today and a streak
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = today
				lastCollectedDate = today
				collectedToday = 1
				dailyPoints = 10
				streak = 2 // The streak is incremented
			}

			assert(dailyReward.remainingToCollect(today) == 0)
			assert(dailyReward.canCollect(today) == 0)
			assert(dailyReward.streak(today) == 1)
			assert(dailyReward.collected(today) == 1)
			assert(dailyReward.dailyPoints(today) == 10)

			dailyReward.delete()
		}

		transaction {
			// Has enough points for a first reward
			val dailyReward = DailyReward.new {
				playerUuid = UUID.randomUUID()
				lastDay = today
				dailyPoints = 100
			}

			assert(dailyReward.remainingToCollect(today) == 1)
			assert(dailyReward.canCollect(today) == 1)
			assert(dailyReward.streak(today) == 0)
			assert(dailyReward.collected(today) == 0)
			assert(dailyReward.dailyPoints(today) == 100)

			dailyReward.delete()
		}
	}
}