package fr.pickaria.vue.rank

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.libraries.datetime.autoFormat
import fr.pickaria.controller.libraries.luckperms.displayName
import fr.pickaria.controller.libraries.luckperms.getGroup
import fr.pickaria.controller.libraries.luckperms.luckPermsUser
import fr.pickaria.controller.libraries.luckperms.save
import fr.pickaria.controller.rank.calculateRankUpgradePrice
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Shard
import fr.pickaria.model.rank.Rank
import fr.pickaria.model.rank.rankConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import net.luckperms.api.model.data.DataMutateResult
import net.luckperms.api.node.Node
import net.luckperms.api.node.NodeType
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors
import kotlin.time.toKotlinDuration


@CommandAlias("ranks")
@CommandPermission("pickaria.command.ranks")
@Description("Gestion des grades premium.")
class RankCommand(manager: PaperCommandManager) : BaseCommand() {
	init {
		manager.commandContexts.registerContext(Rank::class.java) {
			val arg = it.popFirstArg()
			rankConfig.ranks[arg] ?: throw InvalidCommandArgument("Rank of type '$arg' does not exists.")
		}

		manager.commandCompletions.registerCompletion("ranks") {
			rankConfig.ranks.keys
		}
	}

	@Subcommand("buy")
	@CommandCompletion("@ranks")
	@Description("Acheter un nouveau grade.")
	fun onBuy(player: Player, rank: Rank) {
		if (!player.has(Shard, rank.price)) {
			throw ConditionFailedException("Vous n'avez pas assez d'éclats pour acheter ce grade.")
		}

		if (player.hasPermission(rank.permission)) {
			throw ConditionFailedException("Vous avez déjà ce grade ou un grade supérieur à celui-ci.")
		}

		val node = Node.builder(rank.permission).expiry(rank.duration.inWholeSeconds, TimeUnit.SECONDS).build()
		val user = player.luckPermsUser

		when (user.data().add(node)) {
			DataMutateResult.SUCCESS -> {
				val price = player.calculateRankUpgradePrice(rank)

				if (player.withdraw(Shard, price).transactionSuccess()) {
					user.save()
					player.sendMessage(
						Component.text("Vous avez désormais le grade", NamedTextColor.GRAY).appendSpace()
							.append(rank.name).append(Component.text(".", NamedTextColor.GRAY))
					)
				}
			}

			DataMutateResult.FAIL -> throw ConditionFailedException("Quelque chose s'est mal passé.")
			DataMutateResult.FAIL_ALREADY_HAS -> throw ConditionFailedException("Vous avez déjà ce grade ou un grade supérieur à celui-ci.")
			DataMutateResult.FAIL_LACKS -> throw ConditionFailedException("Quelque chose s'est mal passé.")
		}
	}

	@Default
	@Subcommand("menu")
	@Description("Ouvrir le menu des grades.")
	fun onDefault(player: Player) {
		player open "ranks"
	}

	@Subcommand("list")
	@Description("Lister tous ces grades.")
	@CommandPermission("pickaria.developer.ranks")
	fun onList(player: Player) {
		val user = player.luckPermsUser

		val groups = user.getNodes(NodeType.INHERITANCE).stream()
			.map {
				getGroup(it.groupName)?.let { group ->
					val expires = it.expiryDuration?.let { expiry ->
						val formatted = expiry.toKotlinDuration().autoFormat()

						Component.text("Expire dans")
							.appendSpace()
							.append(Component.text(formatted))
					} ?: Component.text("À vie")

					MiniMessage("<gray> ⇒ <group> <expires>") {
						"group" to group.displayName()
						"expires" to expires
					}.toComponent()
				}
			}
			.collect(Collectors.toSet())
			.reduce { acc, component ->
				acc.appendNewline().append(component)
			}

		val message = Component.text("Vos grades :", NamedTextColor.GOLD, TextDecoration.BOLD).appendNewline()
			.append(groups.decoration(TextDecoration.BOLD, TextDecoration.State.FALSE))

		player.sendMessage(message)

	}
}