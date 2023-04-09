package fr.pickaria.vue.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.PaperCommandManager
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.economy.balance
import fr.pickaria.model.economy.Currency
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.model.economy.toController
import fr.pickaria.shared.MiniMessage
import fr.pickaria.shared.give
import org.bukkit.entity.Player

@CommandAlias("money|bal|balance")
@CommandPermission("pickaria.command.balance")
class MoneyCommand : BaseCommand() {
	companion object {
		fun setupContext(manager: PaperCommandManager) {
			manager.commandContexts.registerContext(Currency::class.java) {
				val arg = it.popFirstArg()
				economyConfig.currencies[arg] ?: throw InvalidCommandArgument("Le compte '$arg' n'existe pas.")
			}

			manager.commandCompletions.registerCompletion("currencies") {
				economyConfig.currencies.keys
			}
		}
	}

	@Default
	@CommandCompletion("@currencies")
	fun onDefault(player: Player, @Default("credits") currency: Currency) {
		MiniMessage(economyConfig.balanceMessage) {
			"balance" to currency.toController().format(player.balance(currency))
		}.send(player)

		player.give(currency.toController().bundle(1.0))
	}
}