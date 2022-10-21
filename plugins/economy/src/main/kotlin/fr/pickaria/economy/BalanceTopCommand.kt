package fr.pickaria.economy

import fr.pickaria.shared.models.BankAccount
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getServer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BalanceTopCommand : CommandExecutor {
	companion object {
		const val PAGE_SIZE = 8
	}

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
		val page = try {
			(args?.getOrNull(0)?.toInt() ?: 0).coerceAtLeast(0)
		} catch (_: NumberFormatException) {
			0
		}

		val min = page * PAGE_SIZE
		val players = getServer().offlinePlayers

		if (min > players.size) {
			sender.sendMessage(economyConfig.notMuchPages)
			return true
		}

		val top = BankAccount.top(page, limit = PAGE_SIZE)

		val maxPage = top.size / PAGE_SIZE
		val component = Component.text()

		if (maxPage >= page) {
			component.append(
				miniMessage.deserialize(
					economyConfig.header,
					Placeholder.component("page", Component.text(page + 1)),
					Placeholder.component("max", Component.text(maxPage + 1)),
				)
			)

			top.forEachIndexed { index, account ->
				val player = Bukkit.getOfflinePlayer(account.playerUuid)

				component
					.append(Component.newline())
					.append(
						miniMessage.deserialize(
							economyConfig.row,
							Placeholder.component("position", Component.text(index + 1 + min)),
							Placeholder.component("player", Component.text(player.name ?: "N/A")),
							Placeholder.component("balance", Component.text(economy.format(account.balance))),
						)
					)
			}

			if (maxPage != page) {
				component
					.append(Component.newline())
					.append(
						miniMessage.deserialize(
							economyConfig.footer,
							TagResolver.resolver(
								"next-page",
								Tag.styling(ClickEvent.runCommand("/baltop ${page + 1}"))
							),
							Placeholder.component("page", Component.text(page + 1)),
						)
					)
			}
		} else {
			component.append(economyConfig.notMuchPages)
		}

		sender.sendMessage(component)

		return true
	}
}