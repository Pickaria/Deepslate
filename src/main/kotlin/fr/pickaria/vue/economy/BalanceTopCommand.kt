package fr.pickaria.vue.economy

import co.aikar.commands.BaseCommand
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import fr.pickaria.model.economy.BankAccount
import fr.pickaria.model.economy.Credit
import fr.pickaria.model.economy.economyConfig
import fr.pickaria.shared.MiniMessage
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.tag.Tag
import org.bukkit.Bukkit
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
		val top = BankAccount.top(page, limit = PAGE_SIZE)
		val count = BankAccount.count()
		val maxPage = count / PAGE_SIZE

		if (top.isEmpty()) {
			throw InvalidCommandArgument(economyConfig.notMuchPages)
		}

		val component = Component.text()
			.append(
				+MiniMessage(economyConfig.header) {
					"page" to (page + 1)
					"max" to (maxPage + 1)
				}
			)

		top.forEachIndexed { index, account ->
			val player = Bukkit.getOfflinePlayer(account.playerUuid)

			component
				.append(Component.newline())
				.append(
					+MiniMessage(economyConfig.row) {
						"position" to (index + 1 + pageStart)
						"player" to (player.name ?: "N/A")
						"balance" to Credit.economy.format(account.balance)
					}
				)
		}

		if (count > pageStart + PAGE_SIZE) {
			component
				.append(Component.newline())
				.append(
					+MiniMessage(economyConfig.footer) {
						"next-page" to Tag.styling(ClickEvent.runCommand("/baltop ${page + 1}"))
						"page" to (page + 1)
					}
				)
		}

		player.sendMessage(component)
	}
}