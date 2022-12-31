package fr.pickaria.vue.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import fr.pickaria.controller.libraries.acf.listing
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.LegacyBankAccount
import fr.pickaria.model.economy.economyConfig
import org.bukkit.command.CommandSender

@CommandAlias("baltop|balancetop")
@CommandPermission("pickaria.command.balancetop")
class BalanceTopCommand : BaseCommand() {
	companion object {
		const val PAGE_SIZE = 8
	}

	@Default
	fun onDefault(player: CommandSender, @Default("0") page: Int) {
		if (page < 0) {
			throw InvalidCommandArgument(economyConfig.notMuchPages)
		}

		val pageStart = page * PAGE_SIZE
		val top = LegacyBankAccount.top(page, limit = PAGE_SIZE)
		val count = LegacyBankAccount.count()

		val component = listing(
			economyConfig.header,
			economyConfig.row,
			economyConfig.footer,
			"/baltop",
			page,
			top,
			count
		) { index, account ->
			{
				"position" to (index + 1 + pageStart)
				"player" to (player.name ?: "N/A")
				"balance" to Credit.economy.format(account.balance)
			}
		}

		player.sendMessage(component)
	}
}