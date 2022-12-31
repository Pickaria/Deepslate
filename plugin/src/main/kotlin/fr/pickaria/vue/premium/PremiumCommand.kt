package fr.pickaria.vue.premium

import co.aikar.commands.*
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Subcommand
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.libraries.luckPermsUser
import fr.pickaria.controller.libraries.save
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.premium.Rank
import fr.pickaria.model.premium.premiumConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.luckperms.api.model.data.DataMutateResult
import net.luckperms.api.node.Node
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit


@CommandAlias("premium")
class PremiumCommand : BaseCommand() {
	companion object {
		fun setupContext(manager: PaperCommandManager) {
			manager.commandContexts.registerContext(Rank::class.java) {
				val arg = it.popFirstArg()
				premiumConfig.ranks[arg] ?: throw InvalidCommandArgument("Rank of type '$arg' does not exists.")
			}

			manager.commandCompletions.registerCompletion("ranks") {
				premiumConfig.ranks.keys
			}
		}
	}

	@Subcommand("buy")
	@CommandCompletion("@ranks")
	fun onBuy(player: Player, rank: Rank) {
		if (!player.has(Shard, rank.price.toDouble())) {
			throw ConditionFailedException("Vous n'avez pas assez d'éclats pour acheter ce grade.")
		}

		val node = Node.builder(rank.permission).expiry(rank.duration, TimeUnit.SECONDS).build()

		val user = player.luckPermsUser

		when (user.data().add(node)) {
			DataMutateResult.SUCCESS -> {
				if (player.withdraw(Shard, rank.price.toDouble()).transactionSuccess()) {
					user.save()
					player.sendMessage(Component.text("Vous avez désormais le grade").appendSpace().append(rank.name))
				}
			}

			DataMutateResult.FAIL -> player.sendMessage("Something went wrong.")
			DataMutateResult.FAIL_ALREADY_HAS -> player.sendMessage("Vous avez déjà ce grade.")
			DataMutateResult.FAIL_LACKS -> player.sendMessage("Something went wrong.")
		}
	}

	@Default
	fun onDefault(player: Player) {
		val user = player.luckPermsUser

		val groups = user.getInheritedGroups(user.queryOptions).mapNotNull { group ->
			group.displayName?.let {
				Component.text(" ⇒ ", NamedTextColor.GRAY)
					.append(MiniMessage(it).message)
					.asComponent()
			}
		}.reduce { acc, component ->
			acc.appendNewline().append(component)
		}

		val message =
			Component.text("Vos grades :", NamedTextColor.GOLD, TextDecoration.BOLD)
				.appendNewline()
				.append(groups.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))
		player.sendMessage(message)
	}
}