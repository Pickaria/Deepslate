package fr.pickaria.economy

import fr.pickaria.shared.toComponent
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.Bukkit.getLogger
import org.bukkit.Bukkit.getServer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PayCommand : CommandExecutor, TabCompleter {
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if (sender is Player) {
			val recipient = try {
				getServer().getOfflinePlayer(args[0])
			} catch (_: ArrayIndexOutOfBoundsException) {
				return false
			}

			if (recipient == sender) {
				sender.sendMessage(Config.cantSendToYourself)
				return true
			}

			if (!recipient.hasPlayedBefore()) {
				sender.sendMessage(Config.playerDoesNotExist)
				return true
			}

			val amount = try {
				args[1].toDouble()
			} catch (_: NumberFormatException) {
				sender.sendMessage(Config.amountIsNan)
				return true
			} catch (_: ArrayIndexOutOfBoundsException) {
				return false
			}

			if (amount <= 0.01) {
				sender.sendMessage(Config.lessThanMinimumAmount)
				return true
			}

			if (sender has amount) {
				val withdrawResponse = economy.withdrawPlayer(sender, amount)

				if (withdrawResponse.type == EconomyResponse.ResponseType.SUCCESS) {
					val depositResponse = economy.depositPlayer(recipient, withdrawResponse.amount)

					if (depositResponse.type != EconomyResponse.ResponseType.SUCCESS) {
						sender.sendMessage(Config.receiveError)

						// Try to refund
						val refund = economy.depositPlayer(sender, withdrawResponse.amount)
						if (refund.type == EconomyResponse.ResponseType.FAILURE) {
							getLogger().severe("Can't refund player, withdrew amount: ${withdrawResponse.amount}")
							sender.sendMessage(Config.refundError)
						}
					} else {
						val format = economy.format(depositResponse.amount)
						sender.sendMessage(miniMessage.deserialize(Config.sendSuccess, Placeholder.unparsed("amount", format)))

						if (recipient.isOnline) {
							val message = miniMessage.deserialize(
								Config.receiveSuccess,
								Placeholder.component("sender", sender.displayName()),
								Placeholder.unparsed("amount", format),
							)
							(recipient as Player).sendMessage(message)
						}
					}
				} else {
					sender.sendMessage(Config.sendError)
				}
			} else {
				sender.sendMessage(Config.notEnoughMoney)
			}
		}

		return true
	}

	override fun onTabComplete(
		sender: CommandSender,
		command: Command,
		alias: String,
		args: Array<out String>
	): MutableList<String> = if (args.size == 1) {
			getServer().onlinePlayers.filter {
				it.name.startsWith(args[0]) && sender !== it
			}.map {
				it.name
			}.toMutableList()
		} else {
			mutableListOf()
		}
}