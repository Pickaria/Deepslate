package fr.pickaria.vue.town

import co.aikar.commands.*
import co.aikar.commands.annotation.*
import co.aikar.commands.annotation.Optional
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import fr.pickaria.controller.acf.listing
import fr.pickaria.controller.economy.deposit
import fr.pickaria.controller.economy.has
import fr.pickaria.controller.economy.withdraw
import fr.pickaria.controller.town.*
import fr.pickaria.menu.open
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.town.ResidentRank
import fr.pickaria.model.town.TownPermission
import fr.pickaria.model.town.townConfig
import fr.pickaria.shared.MiniMessage
import kotlinx.datetime.toJavaLocalDateTime
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.tag.Tag
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

@CommandAlias("town")
@CommandPermission("pickaria.command.town")
@Description("Gestion de la ville.")
class TownCommand : BaseCommand() {
	companion object {
		const val PAGE_SIZE = 8

		fun setupContext(manager: PaperCommandManager) {
			manager.commandCompletions.registerCompletion("town_names") {
				TownController.all().map {
					it.identifier
				}
			}
			manager.commandCompletions.registerCompletion("town_residents") { context ->
				context.player.town?.let { town ->
					town.residents().map { it.name }
				}
			}

			manager.commandContexts.registerContext(TownController::class.java) {
				val arg = it.popFirstArg()

				try {
					(TownController[arg] ?: it.player.town)!!
				} catch (_: NullPointerException) {
					throw InvalidCommandArgument("Town '$arg' does not exists.")
				}
			}

			manager.commandConditions.addCondition("has_town") {
				if (!it.issuer.player.hasTown()) {
					throw ConditionFailedException(townConfig.notInTown)
				}
			}

			manager.commandConditions.addCondition("can_leave") {
				if (!it.issuer.player.hasTown()) {
					throw ConditionFailedException(townConfig.notInTown)
				}

				val isMayor = it.issuer.player.residentRank == ResidentRank.MAYOR
				val residents = it.issuer.player.town?.residentCount ?: 0L
				if (isMayor && residents > 1) {
					throw ConditionFailedException("Le maire ne peut pas quitter une ville avec des habitants.")
				}
			}
		}
	}

	@HelpCommand
	@Syntax("")
	fun doHelp(help: CommandHelp) {
		help.showHelp()
	}

	@Subcommand("list")
	@Syntax("[page=0]")
	@Description("Liste toutes les villes.")
	fun onList(player: Player, @Default("0") page: Int) {
		if (page < 0) {
			throw InvalidCommandArgument(economyConfig.notMuchPages)
		}

		val pageStart = page * PAGE_SIZE
		val towns = TownController.all(PAGE_SIZE, pageStart.toLong())
		val count = TownController.count()

		val component = listing(
			townConfig.header, townConfig.townRow, townConfig.townFooter, "/town list", page, towns, count
		) { index, town ->
			{
				"position" to (index + 1 + pageStart)
				"town" to town.identifier
			}
		}

		player.sendMessage(component)
	}

	@CommandAlias("towns")
	@Subcommand("menu")
	fun onMenu(player: Player) {
		player open "towns"
	}

	@Subcommand("teleport|spawn")
	@Syntax("[town name=current town]")
	@CommandCompletion("@town_names")
	@Description("Se téléporter à une ville.")
	fun onTeleport(player: Player, town: TownController) {

	}

	@Subcommand("residents")
	@Syntax("[town name=current town]")
	@CommandCompletion("@town_names")
	@Description("Affiche la liste des résidents d'une ville.")
	fun onResidents(player: Player, town: TownController, @Default("0") page: Int) {
		if (page < 0) {
			throw InvalidCommandArgument(economyConfig.notMuchPages)
		}

		val pageStart = page * PAGE_SIZE
		val residents = town.residents(PAGE_SIZE, pageStart.toLong())
		val count = town.residentCount

		val component = listing(
			townConfig.header,
			townConfig.residentRow,
			townConfig.residentFooter,
			"/town residents",
			page,
			residents,
			count
		) { index, resident ->
			{
				"position" to (index + 1 + pageStart)
				"resident" to resident.name
				"rank" to (resident.residentRank?.name ?: "")
			}
		}

		player.sendMessage(component)
	}

	@Subcommand("info")
	@Syntax("[town name=current town]")
	@CommandCompletion("@town_names")
	@Description("Affiche les informations pour une ville.")
	fun onInfo(player: Player, @Optional town: TownController?) {
		(town ?: player.town)?.let {
			val formatted = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.FRENCH)
				.format(it.creationDate.toJavaLocalDateTime())

			MiniMessage(townConfig.townInfo) {
				"town" to it.identifier
				"balance" to Credit.economy.format(it.balance)
				"residents" to it.residentCount
				"creation" to formatted
			}.send(player)
		} ?: throw ConditionFailedException(townConfig.notInTown)
	}

	@Subcommand("join")
	@Syntax("<town name>")
	@CommandCompletion("@town_names")
	@Description("Rejoint une ville.")
	fun onJoin(player: Player, town: TownController) {
		if (player.hasTown()) {
			throw ConditionFailedException("Vous avez déjà une ville.")
		}

		if (player.hasTownPermission(town, TownPermission.JOIN)) {
			player.joinTown(town)
			MiniMessage(townConfig.joinedTown) {
				"town" to town.identifier
			}.send(player)
		} else {
			throw ConditionFailedException(townConfig.noPermission)
		}
	}

	@Subcommand("leave")
	@Conditions("can_leave")
	@Description("Quitte sa ville actuelle.")
	fun onLeave(player: Player) {
		player.town?.let {
			player.leaveTown(it)
			MiniMessage(townConfig.leftTown) {
				"town" to it.identifier
			}.send(player)
		}
	}

	@Subcommand("deposit")
	@Syntax("<amount>")
	@Conditions("has_town")
	@Description("Dépose de l'argent dans la banque de la ville.")
	fun onDeposit(player: Player, amount: Double) {
		if (!player.has(Credit, amount)) {
			throw ConditionFailedException("Vous n'avez pas cette quantité d'argent.")
		}

		player.town?.let {
			if (!player.hasTownPermission(it, TownPermission.DEPOSIT)) {
				throw ConditionFailedException(townConfig.noPermission)
			}
			if (player.withdraw(Credit, amount).transactionSuccess()) {
				it.deposit(amount)
				MiniMessage(townConfig.depositSuccess) {
					"amount" to amount
				}.send(player)
			}
		} ?: throw ConditionFailedException(townConfig.notInTown)
	}

	@Subcommand("withdraw")
	@Syntax("<amount>")
	@Conditions("has_town")
	@Description("Retire de l'argent de la banque de la ville.")
	fun onWithdraw(player: Player, amount: Double) {
		if (!player.has(Credit, amount)) {
			throw ConditionFailedException("Vous n'avez pas cette quantité d'argent.")
		}

		player.town?.let {
			if (!player.hasTownPermission(it, TownPermission.WITHDRAW)) {
				throw ConditionFailedException(townConfig.noPermission)
			}
			if (player.deposit(Credit, amount).transactionSuccess()) {
				it.withdraw(amount)
				MiniMessage(townConfig.withdrawSuccess) {
					"amount" to amount
				}.send(player)
			}
		} ?: throw ConditionFailedException("Vous n'êtes pas dans une ville.")
	}

	@Subcommand("claim")
	@Conditions("has_town")
	@Description("Indexe le territoire sur lequel se trouve le joueur.")
	fun onClaim(player: Player) {

	}

	@Subcommand("unclaim")
	@Conditions("has_town")
	@Description("Désindexe le territoire sur lequel se trouve le joueur.")
	fun onUnclaim(player: Player) {

	}

	@Subcommand("invite")
	@Syntax("[player name]")
	@CommandCompletion("@players")
	@Conditions("has_town")
	@Description("Invite un joueur à rejoindre votre ville.")
	fun onInvite(player: CommandSender, onlinePlayer: OnlinePlayer) {
		(player as Player).town?.let {
			MiniMessage(townConfig.inviteReceived) {
				"player" to player.name()
				"town" to it.identifier
				"command" to Tag.styling(ClickEvent.runCommand("/town join ${it.identifier}"))
			}.send(onlinePlayer.player)

			MiniMessage(townConfig.inviteSent) {
				"player" to player.name()
				"town" to it.identifier
			}.send(player)
		} ?: throw ConditionFailedException(townConfig.notInTown)
	}

	@Subcommand("kick")
	@Syntax("[player name]")
	@CommandCompletion("@town_residents")
	@Conditions("has_town")
	@Description("Éjecte un joueur de votre ville.")
	fun onKick(player: CommandSender, onlinePlayer: OnlinePlayer) {
		(player as Player).town?.let { town ->
			if (player.hasTownPermission(town, TownPermission.KICK)) {
				onlinePlayer.player.let {
					if (it.town?.id != town.id) {
						throw ConditionFailedException("Le joueur n'est pas dans cette ville.")
					}

					it.leaveTown(town)

					MiniMessage(townConfig.kicked) {
						"town" to town.identifier
					}.send(it)

					MiniMessage(townConfig.kickSuccess) {
						"player" to it.name()
						"town" to town.identifier
					}.send(player)
				}
			} else {
				throw ConditionFailedException(townConfig.noPermission)
			}
		} ?: throw ConditionFailedException(townConfig.notInTown)
	}

	@Subcommand("promote|demote")
	@Syntax("[player name] [rank]")
	@CommandCompletion("@players @ranks")
	@Conditions("has_town")
	@Description("Invite un joueur à rejoindre votre ville.")
	fun onPromote(player: CommandSender, onlinePlayer: OnlinePlayer, rank: ResidentRank) {

	}
}