package fr.pickaria.economy

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit.getServer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class PayCommand : CommandExecutor, TabCompleter, CurrencyExtensions(Credit) {
	@OptIn(GlobalCurrencyExtensions::class)
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

			if (!recipient.hasPlayedBefore() && !recipient.isOnline) {
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

			when (sendTo(sender, recipient, amount)) {
				SendResponse.RECEIVE_ERROR -> sender.sendMessage(Config.receiveError)
				SendResponse.REFUND_ERROR -> sender.sendMessage(Config.refundError)
				SendResponse.SUCCESS -> {
					val format = economy.format(amount)

					if (recipient.isOnline) {
						val message = miniMessage.deserialize(
							Config.receiveSuccess,
							Placeholder.component("sender", sender.displayName()),
							Placeholder.unparsed("amount", format),
						)
						(recipient as Player).sendMessage(message)
					}

					sender.sendMessage(
						miniMessage.deserialize(
							Config.sendSuccess,
							Placeholder.unparsed("amount", format)
						)
					)
				}

				SendResponse.SEND_ERROR -> sender.sendMessage(Config.sendError)
				SendResponse.NOT_ENOUGH_MONEY -> sender.sendMessage(Config.notEnoughMoney)
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